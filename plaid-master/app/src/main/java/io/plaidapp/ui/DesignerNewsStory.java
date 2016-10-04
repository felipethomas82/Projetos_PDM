/*
 * Copyright 2015 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.plaidapp.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.PendingIntent;
import android.app.SharedElementCallback;
import android.app.assist.AssistContent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Path;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsIntent;
import android.support.customtabs.CustomTabsSession;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.style.TextAppearanceSpan;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindDimen;
import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;
import in.uncod.android.bypass.Bypass;
import in.uncod.android.bypass.style.ImageLoadingSpan;
import io.plaidapp.R;
import io.plaidapp.data.api.designernews.UpvoteStoryService;
import io.plaidapp.data.api.designernews.model.Comment;
import io.plaidapp.data.api.designernews.model.Story;
import io.plaidapp.data.api.designernews.model.StoryResponse;
import io.plaidapp.data.prefs.DesignerNewsPrefs;
import io.plaidapp.ui.drawable.ThreadedCommentDrawable;
import io.plaidapp.ui.recyclerview.SlideInItemAnimator;
import io.plaidapp.ui.transitions.GravityArcMotion;
import io.plaidapp.ui.transitions.MorphTransform;
import io.plaidapp.ui.transitions.ReflowText;
import io.plaidapp.ui.widget.AuthorTextView;
import io.plaidapp.ui.widget.CollapsingTitleLayout;
import io.plaidapp.ui.widget.ElasticDragDismissFrameLayout;
import io.plaidapp.ui.widget.PinnedOffsetView;
import io.plaidapp.util.HtmlUtils;
import io.plaidapp.util.ImageUtils;
import io.plaidapp.util.ImeUtils;
import io.plaidapp.util.ViewUtils;
import io.plaidapp.util.customtabs.CustomTabActivityHelper;
import io.plaidapp.util.glide.CircleTransform;
import io.plaidapp.util.glide.ImageSpanTarget;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static io.plaidapp.util.AnimUtils.getFastOutLinearInInterpolator;
import static io.plaidapp.util.AnimUtils.getFastOutSlowInInterpolator;
import static io.plaidapp.util.AnimUtils.getLinearOutSlowInInterpolator;

public class DesignerNewsStory extends Activity {

    protected static final String EXTRA_STORY = "story";
    private static final int RC_LOGIN_UPVOTE = 7;

    private View header;
    @BindView(R.id.comments_list) RecyclerView commentsList;
    private LinearLayoutManager layoutManager;
    private DesignerNewsCommentsAdapter commentsAdapter;
    @BindView(R.id.fab) ImageButton fab;
    @BindView(R.id.fab_expand) View fabExpand;
    @BindView(R.id.comments_container) ElasticDragDismissFrameLayout draggableFrame;
    private ElasticDragDismissFrameLayout.SystemChromeFader chromeFader;
    @Nullable @BindView(R.id.backdrop_toolbar) CollapsingTitleLayout collapsingToolbar;
    @Nullable @BindView(R.id.story_title_background) PinnedOffsetView toolbarBackground;
    @Nullable @BindView(R.id.background) View background;
    private TextView upvoteStory;
    private EditText enterComment;
    private ImageButton postComment;
    @BindInt(R.integer.fab_expand_duration) int fabExpandDuration;
    @BindDimen(R.dimen.comment_thread_width) int threadWidth;
    @BindDimen(R.dimen.comment_thread_gap) int threadGap;

    private Story story;
    private DesignerNewsPrefs designerNewsPrefs;
    private Bypass markdown;
    private CustomTabActivityHelper customTab;
    private CircleTransform circleTransform;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_designer_news_story);
        ButterKnife.bind(this);

        story = getIntent().getParcelableExtra(EXTRA_STORY);
        fab.setOnClickListener(fabClick);
        chromeFader = new ElasticDragDismissFrameLayout.SystemChromeFader(this);
        markdown = new Bypass(this, new Bypass.Options()
                .setBlockQuoteLineColor(
                        ContextCompat.getColor(this, R.color.designer_news_quote_line))
                .setBlockQuoteLineWidth(2) // dps
                .setBlockQuoteLineIndent(8) // dps
                .setPreImageLinebreakHeight(4) //dps
                .setBlockQuoteIndentSize(TypedValue.COMPLEX_UNIT_DIP, 2f)
                .setBlockQuoteTextColor(ContextCompat.getColor(this, R.color.designer_news_quote)));
        circleTransform = new CircleTransform(this);
        designerNewsPrefs = DesignerNewsPrefs.get(this);
        layoutManager = new LinearLayoutManager(this);
        commentsList.setLayoutManager(layoutManager);
        commentsList.setItemAnimator(new CommentAnimator(
                getResources().getInteger(R.integer.comment_expand_collapse_duration)));
        header = getLayoutInflater().inflate(
                R.layout.designer_news_story_description, commentsList, false);
        bindDescription();

        // setup title/toolbar
        if (collapsingToolbar != null) { // narrow device: collapsing toolbar
            collapsingToolbar.addOnLayoutChangeListener(titlebarLayout);
            collapsingToolbar.setTitle(story.title);
            final Toolbar toolbar = (Toolbar) findViewById(R.id.story_toolbar);
            toolbar.setNavigationOnClickListener(backClick);
            commentsList.addOnScrollListener(headerScrollListener);

            setEnterSharedElementCallback(new SharedElementCallback() {
                @Override
                public void onSharedElementStart(List<String> sharedElementNames, List<View>
                        sharedElements, List<View> sharedElementSnapshots) {
                    ReflowText.setupReflow(getIntent(), collapsingToolbar);
                }

                @Override
                public void onSharedElementEnd(List<String> sharedElementNames, List<View>
                        sharedElements, List<View> sharedElementSnapshots) {
                    ReflowText.setupReflow(collapsingToolbar);
                }
            });

        } else { // w600dp configuration: content card scrolls over title bar
            final TextView title = (TextView) findViewById(R.id.story_title);
            title.setText(story.title);
            findViewById(R.id.back).setOnClickListener(backClick);
        }

        final View enterCommentView = setupCommentField();
        if (story.comment_count > 0) {
            // flatten the comments from a nested structure {@see Comment#comments} to a
            // list appropriate for our adapter (using the depth attribute).
            List<Comment> flattened = new ArrayList<>(story.comment_count);
            unnestComments(story.comments, flattened);
            commentsAdapter =
                    new DesignerNewsCommentsAdapter(header, flattened, enterCommentView);
            commentsList.setAdapter(commentsAdapter);

        } else {
            commentsAdapter = new DesignerNewsCommentsAdapter(
                    header, new ArrayList<Comment>(0), enterCommentView);
            commentsList.setAdapter(commentsAdapter);
        }
        customTab = new CustomTabActivityHelper();
        customTab.setConnectionCallback(customTabConnect);
    }

    @Override
    protected void onStart() {
        super.onStart();
        customTab.bindCustomTabsService(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // clean up after any fab expansion
        fab.setAlpha(1f);
        fabExpand.setVisibility(View.INVISIBLE);
        draggableFrame.addListener(chromeFader);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RC_LOGIN_UPVOTE:
                if (resultCode == RESULT_OK) {
                    upvoteStory();
                }
                break;
        }
    }

    @Override
    protected void onPause() {
        draggableFrame.removeListener(chromeFader);
        super.onPause();
    }

    @Override
    protected void onStop() {
        customTab.unbindCustomTabsService(this);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        customTab.setConnectionCallback(null);
        super.onDestroy();
    }

    @Override @TargetApi(Build.VERSION_CODES.M)
    public void onProvideAssistContent(AssistContent outContent) {
        outContent.setWebUri(Uri.parse(story.url));
    }

    public static CustomTabsIntent.Builder getCustomTabIntent(@NonNull Context context,
                                                              @NonNull Story story,
                                                              @Nullable CustomTabsSession session) {
        Intent upvoteStory = new Intent(context, UpvoteStoryService.class);
        upvoteStory.setAction(UpvoteStoryService.ACTION_UPVOTE);
        upvoteStory.putExtra(UpvoteStoryService.EXTRA_STORY_ID, story.id);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, upvoteStory, 0);
        return new CustomTabsIntent.Builder(session)
                .setToolbarColor(ContextCompat.getColor(context, R.color.designer_news))
                .setActionButton(ImageUtils.vectorToBitmap(context,
                                R.drawable.ic_upvote_filled_24dp_white),
                        context.getString(R.string.upvote_story),
                        pendingIntent,
                        false)
                .setShowTitle(true)
                .enableUrlBarHiding()
                .addDefaultShareMenuItem();
    }

    private final CustomTabActivityHelper.ConnectionCallback customTabConnect
            = new CustomTabActivityHelper.ConnectionCallback() {

        @Override
        public void onCustomTabsConnected() {
            customTab.mayLaunchUrl(Uri.parse(story.url), null, null);
        }

        @Override public void onCustomTabsDisconnected() { }
    };

    private final RecyclerView.OnScrollListener headerScrollListener
            = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            updateScrollDependentUi();
        }
    };

    private final View.OnClickListener backClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            finishAfterTransition();
        }
    };

    private void updateScrollDependentUi() {
        // feed scroll events to the header
        if (collapsingToolbar != null) {
            final int headerScroll = header.getTop() - commentsList.getPaddingTop();
            collapsingToolbar.setScrollPixelOffset(-headerScroll);
            toolbarBackground.setOffset(headerScroll);
        }
        updateFabVisibility();
    }

    private boolean fabIsVisible = true;
    private void updateFabVisibility() {
        // the FAB position can interfere with the enter comment field. Hide the FAB if:
        // - The comment field is scrolled onto screen
        // - The comment field is focused (i.e. stories with no/few comments might not push the
        //   enter comment field off-screen so need to make sure the button is accessible
        // - A comment reply field is focused
        final boolean enterCommentFocused = enterComment.isFocused();
        final int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
        final int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
        final int footerPosition = commentsAdapter.getItemCount() - 1;
        final boolean footerVisible = lastVisibleItemPosition == footerPosition;
        final boolean replyCommentFocused = commentsAdapter.isReplyToCommentFocused();

        final boolean fabShouldBeVisible =
                ((firstVisibleItemPosition == 0 && !enterCommentFocused) || !footerVisible)
                        && !replyCommentFocused;

        if (!fabShouldBeVisible && fabIsVisible) {
            fabIsVisible = false;
            fab.animate()
                    .scaleX(0f)
                    .scaleY(0f)
                    .alpha(0.6f)
                    .setDuration(200L)
                    .setInterpolator(getFastOutLinearInInterpolator(this))
                    .withLayer()
                    .setListener(postHideFab)
                    .start();
        } else if (fabShouldBeVisible && !fabIsVisible) {
            fabIsVisible = true;
            fab.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .alpha(1f)
                    .setDuration(200L)
                    .setInterpolator(getLinearOutSlowInInterpolator(this))
                    .withLayer()
                    .setListener(preShowFab)
                    .start();
            ImeUtils.hideIme(enterComment);
        }
    }

    private AnimatorListenerAdapter preShowFab = new AnimatorListenerAdapter() {
        @Override
        public void onAnimationStart(Animator animation) {
            fab.setVisibility(View.VISIBLE);
        }
    };

    private AnimatorListenerAdapter postHideFab = new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
            fab.setVisibility(View.GONE);
        }
    };

    // title can expand up to a max number of lines. If it does then adjust UI to reflect
    private View.OnLayoutChangeListener titlebarLayout = new View.OnLayoutChangeListener() {
        @Override
        public void onLayoutChange(View v, int left, int top, int right, int bottom, int
                oldLeft, int oldTop, int oldRight, int oldBottom) {
            if ((bottom - top) != (oldBottom - oldTop)) {
                commentsList.setPaddingRelative(commentsList.getPaddingStart(),
                        collapsingToolbar.getHeight(),
                        commentsList.getPaddingEnd(),
                        commentsList.getPaddingBottom());
                commentsList.scrollToPosition(0);
            }
            collapsingToolbar.removeOnLayoutChangeListener(this);
        }
    };

    private View.OnClickListener fabClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            doFabExpand();
            CustomTabActivityHelper.openCustomTab(
                    DesignerNewsStory.this,
                    getCustomTabIntent(DesignerNewsStory.this, story,
                            customTab.getSession())
                            .setStartAnimations(getApplicationContext(),
                                    R.anim.chrome_custom_tab_enter,
                                    R.anim.fade_out_rapidly)
                            .build(),
                    Uri.parse(story.url));
        }
    };

    private void doFabExpand() {
        // translate the chrome placeholder ui so that it is centered on the FAB
        int fabCenterX = (fab.getLeft() + fab.getRight()) / 2;
        int fabCenterY = ((fab.getTop() + fab.getBottom()) / 2) - fabExpand.getTop();
        int translateX = fabCenterX - (fabExpand.getWidth() / 2);
        int translateY = fabCenterY - (fabExpand.getHeight() / 2);
        fabExpand.setTranslationX(translateX);
        fabExpand.setTranslationY(translateY);

        // then reveal the placeholder ui, starting from the center & same dimens as fab
        fabExpand.setVisibility(View.VISIBLE);
        Animator reveal = ViewAnimationUtils.createCircularReveal(
                fabExpand,
                fabExpand.getWidth() / 2,
                fabExpand.getHeight() / 2,
                fab.getWidth() / 2,
                (int) Math.hypot(fabExpand.getWidth() / 2, fabExpand.getHeight() / 2))
                .setDuration(fabExpandDuration);

        // translate the placeholder ui back into position along an arc
        GravityArcMotion arcMotion = new GravityArcMotion();
        arcMotion.setMinimumVerticalAngle(70f);
        Path motionPath = arcMotion.getPath(translateX, translateY, 0, 0);
        Animator position = ObjectAnimator.ofFloat(fabExpand, View.TRANSLATION_X, View
                .TRANSLATION_Y, motionPath)
                .setDuration(fabExpandDuration);

        // animate from the FAB colour to the placeholder background color
        Animator background = ObjectAnimator.ofArgb(fabExpand,
                ViewUtils.BACKGROUND_COLOR,
                ContextCompat.getColor(this, R.color.designer_news),
                ContextCompat.getColor(this, R.color.background_light))
                .setDuration(fabExpandDuration);

        // fade out the fab (rapidly)
        Animator fadeOutFab = ObjectAnimator.ofFloat(fab, View.ALPHA, 0f)
                .setDuration(60);

        // play 'em all together with the material interpolator
        AnimatorSet show = new AnimatorSet();
        show.setInterpolator(getFastOutSlowInInterpolator(DesignerNewsStory.this));
        show.playTogether(reveal, background, position, fadeOutFab);
        show.start();
    }

    private void bindDescription() {
        final TextView storyComment = (TextView) header.findViewById(R.id.story_comment);
        if (!TextUtils.isEmpty(story.comment)) {
            HtmlUtils.parseMarkdownAndSetText(storyComment, story.comment, markdown,
                    new Bypass.LoadImageCallback() {
                @Override
                public void loadImage(String src, ImageLoadingSpan loadingSpan) {
                    Glide.with(DesignerNewsStory.this)
                            .load(src)
                            .asBitmap()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(new ImageSpanTarget(storyComment, loadingSpan));
                }
            });
        } else {
            storyComment.setVisibility(View.GONE);
        }

        upvoteStory = (TextView) header.findViewById(R.id.story_vote_action);
        upvoteStory.setText(getResources().getQuantityString(R.plurals.upvotes, story.vote_count,
                NumberFormat.getInstance().format(story.vote_count)));
        upvoteStory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                upvoteStory();
            }
        });

        final TextView share = (TextView) header.findViewById(R.id.story_share_action);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AnimatedVectorDrawable) share.getCompoundDrawables()[1]).start();
                startActivity(ShareCompat.IntentBuilder.from(DesignerNewsStory.this)
                        .setText(story.url)
                        .setType("text/plain")
                        .setSubject(story.title)
                        .getIntent());
            }
        });

        TextView storyPosterTime = (TextView) header.findViewById(R.id.story_poster_time);
        SpannableString poster = new SpannableString(story.user_display_name.toLowerCase());
        poster.setSpan(new TextAppearanceSpan(this, R.style.TextAppearance_CommentAuthor),
                0, poster.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        CharSequence job =
                !TextUtils.isEmpty(story.user_job) ? "\n" + story.user_job.toLowerCase() : "";
        CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(story.created_at.getTime(),
                System.currentTimeMillis(),
                DateUtils.SECOND_IN_MILLIS)
                .toString().toLowerCase();
        storyPosterTime.setText(TextUtils.concat(poster, job, "\n", timeAgo));
        ImageView avatar = (ImageView) header.findViewById(R.id.story_poster_avatar);
        if (!TextUtils.isEmpty(story.user_portrait_url)) {
            Glide.with(this)
                    .load(story.user_portrait_url)
                    .placeholder(R.drawable.avatar_placeholder)
                    .transform(circleTransform)
                    .into(avatar);
        } else {
            avatar.setVisibility(View.GONE);
        }
    }

    @NonNull
    private View setupCommentField() {
        View enterCommentView = getLayoutInflater()
                .inflate(R.layout.designer_news_enter_comment, commentsList, false);
        enterComment = (EditText) enterCommentView.findViewById(R.id.comment);
        postComment = (ImageButton) enterCommentView.findViewById(R.id.post_comment);
        postComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (designerNewsPrefs.isLoggedIn()) {
                    if (TextUtils.isEmpty(enterComment.getText())) return;
                    enterComment.setEnabled(false);
                    postComment.setEnabled(false);
                    final Call<Comment> comment = designerNewsPrefs.getApi()
                            .comment(story.id, enterComment.getText().toString());
                    comment.enqueue(new Callback<Comment>() {
                        @Override
                        public void onResponse(Call<Comment> call, Response<Comment> response) {
                            enterComment.getText().clear();
                            enterComment.setEnabled(true);
                            postComment.setEnabled(true);
                            commentsAdapter.addComment(response.body());
                        }

                        @Override
                        public void onFailure(Call<Comment> call, Throwable t) {
                            Toast.makeText(getApplicationContext(),
                                    "Failed to post comment :(", Toast.LENGTH_SHORT).show();
                            enterComment.setEnabled(true);
                            postComment.setEnabled(true);
                        }
                    });
                } else {
                    needsLogin(postComment, 0);
                }
                enterComment.clearFocus();
            }
        });
        enterComment.setOnFocusChangeListener(enterCommentFocus);
        return enterCommentView;
    }

    private void upvoteStory() {
        if (designerNewsPrefs.isLoggedIn()) {
            if (!upvoteStory.isActivated()) {
                upvoteStory.setActivated(true);
                final Call<StoryResponse> upvoteStory
                        = designerNewsPrefs.getApi().upvoteStory(story.id);
                upvoteStory.enqueue(new Callback<StoryResponse>() {
                    @Override
                    public void onResponse(Call<StoryResponse> call, Response<StoryResponse>
                            response) {
                        final int newUpvoteCount = response.body().story.vote_count;
                        DesignerNewsStory.this.upvoteStory.setText(getResources().getQuantityString(
                                R.plurals.upvotes, newUpvoteCount,
                                NumberFormat.getInstance().format(newUpvoteCount)));
                    }

                    @Override
                    public void onFailure(Call<StoryResponse> call, Throwable t) {

                    }
                });
            } else {
                upvoteStory.setActivated(false);
                // TODO delete upvote. Not available in v1 API.
            }

        } else {
            needsLogin(upvoteStory, RC_LOGIN_UPVOTE);
        }
    }

    private void needsLogin(View triggeringView, int requestCode) {
        Intent login = new Intent(DesignerNewsStory.this,
                DesignerNewsLogin.class);
        MorphTransform.addExtras(login, ContextCompat.getColor(this, R.color.background_light),
                triggeringView.getHeight() / 2);
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(
                DesignerNewsStory.this,
                triggeringView, getString(R.string.transition_designer_news_login));
        startActivityForResult(login, requestCode, options.toBundle());
    }

    private void unnestComments(List<Comment> nested, List<Comment> flat) {
        for (Comment comment : nested) {
            flat.add(comment);
            if (comment.comments != null && comment.comments.size() > 0) {
                unnestComments(comment.comments, flat);
            }
        }
    }

    private View.OnFocusChangeListener enterCommentFocus = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            // kick off an anim (via animated state list) on the post button. see
            // @drawable/ic_add_comment_state
            postComment.setActivated(hasFocus);
            updateFabVisibility();
        }
    };

    private boolean isOP(Long userId) {
        return userId.equals(story.user_id);
    }

    /* package */ class DesignerNewsCommentsAdapter
            extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int TYPE_HEADER = 0;
        private static final int TYPE_NO_COMMENTS = 1;
        private static final int TYPE_COMMENT = 2;
        private static final int TYPE_COMMENT_REPLY = 3;
        private static final int TYPE_FOOTER = 4;

        private View header;
        private List<Comment> comments;
        private View footer;
        private int expandedCommentPosition = RecyclerView.NO_POSITION;
        private boolean replyToCommentFocused = false;

        DesignerNewsCommentsAdapter(@NonNull View header,
                                    @NonNull List<Comment> comments,
                                    @NonNull View footer) {
            this.header = header;
            this.comments = comments;
            this.footer = footer;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0)  return TYPE_HEADER;
            if (isCommentReplyExpanded() && position == expandedCommentPosition + 1)
                return TYPE_COMMENT_REPLY;
            int footerPosition = hasComments() ? 1 + comments.size() // header + comments
                    : 2; // header + no comments view
            if (isCommentReplyExpanded()) footerPosition++;
            if (position == footerPosition) return TYPE_FOOTER;
            return hasComments() ? TYPE_COMMENT : TYPE_NO_COMMENTS;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            switch (viewType) {
                case TYPE_HEADER:
                    return new HeaderHolder(header);
                case TYPE_COMMENT:
                    return createCommentHolder(parent);
                case TYPE_COMMENT_REPLY:
                    return createCommentReplyHolder(parent);
                case TYPE_NO_COMMENTS:
                    return new NoCommentsHolder(
                        getLayoutInflater().inflate(
                                R.layout.designer_news_no_comments, parent, false));
                case TYPE_FOOTER:
                    return new FooterHolder(footer);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            switch (getItemViewType(position)) {
                case TYPE_COMMENT:
                    bindComment((CommentHolder) holder, null);
                    break;
                case TYPE_COMMENT_REPLY:
                    bindCommentReply((CommentReplyHolder) holder);
                    break;
            } // nothing to bind for header / no comment / footer views
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder,
                                     int position,
                                     List<Object> partialChangePayloads) {
            switch (getItemViewType(position)) {
                case TYPE_COMMENT:
                    bindComment((CommentHolder) holder, partialChangePayloads);
                    break;
                default:
                    onBindViewHolder(holder, position);
            }
        }

        @Override
        public int getItemCount() {
            int itemCount = 2; // header + footer
            if (hasComments()) {
                itemCount += comments.size();
            } else {
                itemCount++; // no comments view
            }
            if (isCommentReplyExpanded()) itemCount++;
            return itemCount;
        }

        public void addComment(Comment newComment) {
            if (!hasComments()) {
                notifyItemRemoved(1); // remove the no comments view
            }
            comments.add(newComment);
            notifyItemInserted(commentIndexToAdapterPosition(comments.size() - 1));
        }

        /**
         * Add a new comment and return the adapter position that it was inserted at.
         */
        public int addCommentReply(Comment newComment, int inReplyToAdapterPosition) {
            // when replying to a comment, we want to insert it after any existing replies
            // i.e. after any following comments with the same or greater depth
            int commentIndex = adapterPositionToCommentIndex(inReplyToAdapterPosition);
            do {
                commentIndex++;
            } while (commentIndex < comments.size() &&
                    comments.get(commentIndex).depth >= newComment.depth);
            comments.add(commentIndex, newComment);
            int adapterPosition = commentIndexToAdapterPosition(commentIndex);
            notifyItemInserted(adapterPosition);
            return adapterPosition;
        }

        public boolean isReplyToCommentFocused() {
            return replyToCommentFocused;
        }

        private boolean hasComments() {
            return !comments.isEmpty();
        }

        private boolean isCommentReplyExpanded() {
            return expandedCommentPosition != RecyclerView.NO_POSITION;
        }

        private Comment getComment(int adapterPosition) {
            return comments.get(adapterPositionToCommentIndex(adapterPosition));
        }

        private int adapterPositionToCommentIndex(int adapterPosition) {
            int index = adapterPosition - 1; // less header
            if (isCommentReplyExpanded()
                    && adapterPosition > expandedCommentPosition) index--;
            return index;
        }

        private int commentIndexToAdapterPosition(int index) {
            int adapterPosition = index + 1; // header
            if (isCommentReplyExpanded()) {
                int expandedCommentIndex = adapterPositionToCommentIndex(expandedCommentPosition);
                if (index > expandedCommentIndex) adapterPosition++;
            }
            return adapterPosition;
        }

        @NonNull
        private CommentHolder createCommentHolder(ViewGroup parent) {
            final CommentHolder holder = new CommentHolder(
                    getLayoutInflater().inflate(R.layout.designer_news_comment, parent, false));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final boolean collapsingSelf =
                            expandedCommentPosition == holder.getAdapterPosition();
                    collapseExpandedComment();
                    if (collapsingSelf) return;

                    // show reply below this
                    expandedCommentPosition = holder.getAdapterPosition();
                    notifyItemInserted(expandedCommentPosition + 1);
                    notifyItemChanged(expandedCommentPosition, CommentAnimator.EXPAND_COMMENT);
                }
            });
            holder.threadDepth.setImageDrawable(
                    new ThreadedCommentDrawable(threadWidth, threadGap));

            return holder;
        }

        private void collapseExpandedComment() {
            if (!isCommentReplyExpanded()) return;
            notifyItemChanged(expandedCommentPosition, CommentAnimator.COLLAPSE_COMMENT);
            notifyItemRemoved(expandedCommentPosition + 1);
            replyToCommentFocused = false;
            expandedCommentPosition = RecyclerView.NO_POSITION;
            updateFabVisibility();
        }

        private void bindComment(final CommentHolder holder, List<Object> partialChanges) {
            // Check if this is a partial update for expanding/collapsing a comment. If it is we
            // can do a partial bind as the bound data has not changed.
            if (partialChanges == null || partialChanges.isEmpty() ||
                    !(partialChanges.contains(CommentAnimator.COLLAPSE_COMMENT)
                        || partialChanges.contains(CommentAnimator.EXPAND_COMMENT))) {

                final Comment comment = getComment(holder.getAdapterPosition());
                HtmlUtils.parseMarkdownAndSetText(holder.comment, comment.body, markdown,
                        new Bypass.LoadImageCallback() {
                    @Override
                    public void loadImage(String src, ImageLoadingSpan loadingSpan) {
                        Glide.with(DesignerNewsStory.this)
                                .load(src)
                                .asBitmap()
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(new ImageSpanTarget(holder.comment, loadingSpan));
                    }
                });
                holder.author.setText(comment.user_display_name.toLowerCase());
                holder.author.setOriginalPoster(isOP(comment.user_id));
                if (comment.created_at != null) {
                    holder.timeAgo.setText(
                            DateUtils.getRelativeTimeSpanString(comment.created_at.getTime(),
                                    System.currentTimeMillis(),
                                    DateUtils.SECOND_IN_MILLIS)
                                    .toString().toLowerCase());
                }
                // FIXME updating drawable doesn't seem to be working, just create a new one
                //((ThreadedCommentDrawable) holder.threadDepth.getDrawable())
                //     .setDepth(comment.depth);

                holder.threadDepth.setImageDrawable(
                        new ThreadedCommentDrawable(threadWidth, threadGap, comment.depth));
            }

            // set/clear expanded comment state
            holder.itemView.setActivated(holder.getAdapterPosition() == expandedCommentPosition);
            if (holder.getAdapterPosition() == expandedCommentPosition) {
                final int threadDepthWidth = holder.threadDepth.getDrawable().getIntrinsicWidth();
                final float leftShift = -(threadDepthWidth + ((ViewGroup.MarginLayoutParams)
                        holder.threadDepth.getLayoutParams()).getMarginEnd());
                holder.author.setTranslationX(leftShift);
                holder.comment.setTranslationX(leftShift);
                holder.threadDepth.setTranslationX(-(threadDepthWidth
                        + ((ViewGroup.MarginLayoutParams)
                        holder.threadDepth.getLayoutParams()).getMarginStart()));
            } else {
                holder.threadDepth.setTranslationX(0f);
                holder.author.setTranslationX(0f);
                holder.comment.setTranslationX(0f);
            }
        }

        @NonNull
        private CommentReplyHolder createCommentReplyHolder(ViewGroup parent) {
            final CommentReplyHolder holder = new CommentReplyHolder(getLayoutInflater()
                    .inflate(R.layout.designer_news_comment_actions, parent, false));

            holder.commentVotes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (designerNewsPrefs.isLoggedIn()) {
                        Comment comment = getComment(holder.getAdapterPosition());
                        if (!holder.commentVotes.isActivated()) {
                            final Call<Comment> upvoteComment =
                                    designerNewsPrefs.getApi().upvoteComment(comment.id);
                            upvoteComment.enqueue(new Callback<Comment>() {
                                @Override
                                public void onResponse(Call<Comment> call,
                                                       Response<Comment> response) { }

                                @Override
                                public void onFailure(Call<Comment> call, Throwable t) { }
                            });
                            comment.upvoted = true;
                            comment.vote_count++;
                            holder.commentVotes.setText(String.valueOf(comment.vote_count));
                            holder.commentVotes.setActivated(true);
                        } else {
                            comment.upvoted = false;
                            comment.vote_count--;
                            holder.commentVotes.setText(String.valueOf(comment.vote_count));
                            holder.commentVotes.setActivated(false);
                            // TODO actually delete upvote
                        }
                    } else {
                        needsLogin(holder.commentVotes, 0);
                    }
                    holder.commentReply.clearFocus();
                }
            });

            holder.postReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (designerNewsPrefs.isLoggedIn()) {
                        if (TextUtils.isEmpty(holder.commentReply.getText())) return;
                        final int inReplyToCommentPosition = holder.getAdapterPosition() - 1;
                        final Comment replyingTo = getComment(inReplyToCommentPosition);
                        collapseExpandedComment();

                        // insert a locally created comment before actually
                        // hitting the API for immediate response
                        int replyDepth = replyingTo.depth + 1;
                        final int newReplyPosition = commentsAdapter.addCommentReply(
                                new Comment.Builder()
                                        .setBody(holder.commentReply.getText().toString())
                                        .setCreatedAt(new Date())
                                        .setDepth(replyDepth)
                                        .setUserId(designerNewsPrefs.getUserId())
                                        .setUserDisplayName(designerNewsPrefs.getUserName())
                                        .setUserPortraitUrl(designerNewsPrefs.getUserAvatar())
                                        .build(),
                                inReplyToCommentPosition);
                        final Call<Comment> replyToComment = designerNewsPrefs.getApi()
                                .replyToComment(replyingTo.id,
                                        holder.commentReply.getText().toString());
                        replyToComment.enqueue(new Callback<Comment>() {
                            @Override
                            public void onResponse(Call<Comment> call, Response<Comment> response) {

                            }

                            @Override
                            public void onFailure(Call<Comment> call, Throwable t) {
                                Toast.makeText(getApplicationContext(),
                                        "Failed to post comment :(", Toast.LENGTH_SHORT).show();
                            }
                        });
                        holder.commentReply.getText().clear();
                        ImeUtils.hideIme(holder.commentReply);
                        commentsList.scrollToPosition(newReplyPosition);
                    } else {
                        needsLogin(holder.postReply, 0);
                    }
                    holder.commentReply.clearFocus();
                }
            });

            holder.commentReply.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    replyToCommentFocused = hasFocus;
                    final Interpolator interp = getFastOutSlowInInterpolator(holder
                            .itemView.getContext());
                    if (hasFocus) {
                        holder.commentVotes.animate()
                                .translationX(-holder.commentVotes.getWidth())
                                .alpha(0f)
                                .setDuration(200L)
                                .setInterpolator(interp);
                        holder.replyLabel.animate()
                                .translationX(-holder.commentVotes.getWidth())
                                .setDuration(200L)
                                .setInterpolator(interp);
                        holder.postReply.setVisibility(View.VISIBLE);
                        holder.postReply.setAlpha(0f);
                        holder.postReply.animate()
                                .alpha(1f)
                                .setDuration(200L)
                                .setInterpolator(interp)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationStart(Animator animation) {
                                        holder.itemView.setHasTransientState(true);
                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        holder.itemView.setHasTransientState(false);
                                    }
                                });
                        updateFabVisibility();
                    } else {
                        holder.commentVotes.animate()
                                .translationX(0f)
                                .alpha(1f)
                                .setDuration(200L)
                                .setInterpolator(interp);
                        holder.replyLabel.animate()
                                .translationX(0f)
                                .setDuration(200L)
                                .setInterpolator(interp);
                        holder.postReply.animate()
                                .alpha(0f)
                                .setDuration(200L)
                                .setInterpolator(interp)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationStart(Animator animation) {
                                        holder.itemView.setHasTransientState(true);
                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        holder.postReply.setVisibility(View.INVISIBLE);
                                        holder.itemView.setHasTransientState(true);
                                    }
                                });
                        updateFabVisibility();
                    }
                    holder.postReply.setActivated(hasFocus);
                }
            });

            return holder;
        }

        private void bindCommentReply(CommentReplyHolder holder) {
            Comment comment = getComment(holder.getAdapterPosition() - 1);
            holder.commentVotes.setText(String.valueOf(comment.vote_count));
            holder.commentVotes.setActivated(comment.upvoted != null && comment.upvoted);
        }
    }

    /* package */ static class CommentHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.depth) ImageView threadDepth;
        @BindView(R.id.comment_author) AuthorTextView author;
        @BindView(R.id.comment_time_ago) TextView timeAgo;
        @BindView(R.id.comment_text) TextView comment;

        public CommentHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    /* package */ static class CommentReplyHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.comment_votes) Button commentVotes;
        @BindView(R.id.comment_reply_label) TextInputLayout replyLabel;
        @BindView(R.id.comment_reply) EditText commentReply;
        @BindView(R.id.post_reply) ImageButton postReply;

        public CommentReplyHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

    /* package */ static class HeaderHolder extends RecyclerView.ViewHolder {

        public HeaderHolder(View itemView) {
            super(itemView);
        }
    }

    /* package */ static class NoCommentsHolder extends RecyclerView.ViewHolder {

        public NoCommentsHolder(View itemView) {
            super(itemView);
        }
    }

    /* package */ static class FooterHolder extends RecyclerView.ViewHolder {

        public FooterHolder(View itemView) {
            super(itemView);
        }
    }

    private static class CommentAnimator extends SlideInItemAnimator {

        CommentAnimator(long addRemoveDuration) {
            super();
            setAddDuration(addRemoveDuration);
            setRemoveDuration(addRemoveDuration);
        }

        public static final int EXPAND_COMMENT = 1;
        public static final int COLLAPSE_COMMENT = 2;

        @Override
        public boolean canReuseUpdatedViewHolder(RecyclerView.ViewHolder viewHolder) {
            return true;
        }

        @NonNull
        @Override
        public ItemHolderInfo recordPreLayoutInformation(RecyclerView.State state,
                                                         RecyclerView.ViewHolder viewHolder,
                                                         int changeFlags,
                                                         List<Object> payloads) {
            CommentItemHolderInfo info = (CommentItemHolderInfo)
                    super.recordPreLayoutInformation(state, viewHolder, changeFlags, payloads);
            info.doExpand = payloads.contains(EXPAND_COMMENT);
            info.doCollapse = payloads.contains(COLLAPSE_COMMENT);
            return info;
        }

        @Override
        public boolean animateChange(RecyclerView.ViewHolder oldHolder,
                                     RecyclerView.ViewHolder newHolder,
                                     ItemHolderInfo preInfo,
                                     ItemHolderInfo postInfo) {
            if (newHolder instanceof CommentHolder && preInfo instanceof CommentItemHolderInfo) {
                final CommentHolder holder = (CommentHolder) newHolder;
                final CommentItemHolderInfo info = (CommentItemHolderInfo) preInfo;
                final float expandedThreadOffset = -(holder.threadDepth.getWidth() + ((ViewGroup
                        .MarginLayoutParams) holder.threadDepth.getLayoutParams())
                        .getMarginStart());
                final float expandedAuthorCommentOffset = -(holder.threadDepth.getWidth() +
                        ((ViewGroup.MarginLayoutParams) holder.threadDepth.getLayoutParams())
                                .getMarginEnd());

                if (info.doExpand) {
                    Interpolator moveInterpolator = getFastOutSlowInInterpolator(holder
                            .itemView.getContext());
                    holder.threadDepth.setTranslationX(0f);
                    holder.threadDepth.animate()
                            .translationX(expandedThreadOffset)
                            .setDuration(160L)
                            .setInterpolator(moveInterpolator);
                    holder.author.setTranslationX(0f);
                    holder.author.animate()
                            .translationX(expandedAuthorCommentOffset)
                            .setDuration(320L)
                            .setInterpolator(moveInterpolator);
                    holder.comment.setTranslationX(0f);
                    holder.comment.animate()
                            .translationX(expandedAuthorCommentOffset)
                            .setDuration(320L)
                            .setInterpolator(moveInterpolator)
                            .setListener(new AnimatorListenerAdapter() {

                                @Override
                                public void onAnimationStart(Animator animation) {
                                    dispatchChangeStarting(holder, false);
                                    holder.itemView.setHasTransientState(true);
                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    holder.itemView.setHasTransientState(false);
                                    dispatchChangeFinished(holder, false);
                                }
                            });
                } else if (info.doCollapse) {
                    Interpolator enterInterpolator = getLinearOutSlowInInterpolator
                            (holder.itemView
                            .getContext());
                    Interpolator moveInterpolator = getFastOutSlowInInterpolator(holder
                            .itemView
                            .getContext());

                    // return the thread depth indicator into place
                    holder.threadDepth.setTranslationX(expandedThreadOffset);
                    holder.threadDepth.animate()
                            .translationX(0f)
                            .setDuration(200L)
                            .setInterpolator(enterInterpolator)
                            .setListener(new AnimatorListenerAdapter() {

                                @Override
                                public void onAnimationStart(Animator animation) {
                                    dispatchChangeStarting(holder, false);
                                    holder.itemView.setHasTransientState(true);
                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    holder.itemView.setHasTransientState(false);
                                    dispatchChangeFinished(holder, false);
                                }
                            });

                    // return the text into place
                    holder.author.setTranslationX(expandedAuthorCommentOffset);
                    holder.author.animate()
                            .translationX(0f)
                            .setDuration(200L)
                            .setInterpolator(moveInterpolator);
                    holder.comment.setTranslationX(expandedAuthorCommentOffset);
                    holder.comment.animate()
                            .translationX(0f)
                            .setDuration(200L)
                            .setInterpolator(moveInterpolator);
                }
            }
            return super.animateChange(oldHolder, newHolder, preInfo, postInfo);
        }

        @Override
        public ItemHolderInfo obtainHolderInfo() {
            return new CommentItemHolderInfo();
        }

        /* package */ static class CommentItemHolderInfo extends ItemHolderInfo {
            boolean doExpand;
            boolean doCollapse;
        }
    }
}
