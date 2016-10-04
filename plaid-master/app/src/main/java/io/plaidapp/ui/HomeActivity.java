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
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.StyleSpan;
import android.transition.TransitionManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.WindowInsets;
import android.view.animation.AnimationUtils;
import android.widget.ActionMenuView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.plaidapp.R;
import io.plaidapp.data.DataManager;
import io.plaidapp.data.PlaidItem;
import io.plaidapp.data.Source;
import io.plaidapp.data.api.designernews.PostStoryService;
import io.plaidapp.data.api.designernews.model.Story;
import io.plaidapp.data.pocket.PocketUtils;
import io.plaidapp.data.prefs.DesignerNewsPrefs;
import io.plaidapp.data.prefs.DribbblePrefs;
import io.plaidapp.data.prefs.SourceManager;
import io.plaidapp.ui.recyclerview.FilterTouchHelperCallback;
import io.plaidapp.ui.recyclerview.GridItemDividerDecoration;
import io.plaidapp.ui.recyclerview.InfiniteScrollListener;
import io.plaidapp.ui.transitions.FabTransform;
import io.plaidapp.ui.transitions.MorphTransform;
import io.plaidapp.util.AnimUtils;
import io.plaidapp.util.ViewUtils;


public class HomeActivity extends Activity {

    private static final int RC_SEARCH = 0;
    private static final int RC_AUTH_DRIBBBLE_FOLLOWING = 1;
    private static final int RC_AUTH_DRIBBBLE_USER_LIKES = 2;
    private static final int RC_AUTH_DRIBBBLE_USER_SHOTS = 3;
    private static final int RC_NEW_DESIGNER_NEWS_STORY = 4;
    private static final int RC_NEW_DESIGNER_NEWS_LOGIN = 5;

    @BindView(R.id.drawer) DrawerLayout drawer;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.grid) RecyclerView grid;
    @BindView(R.id.fab) ImageButton fab;
    @BindView(R.id.filters) RecyclerView filtersList;
    @BindView(android.R.id.empty) ProgressBar loading;
    @Nullable @BindView(R.id.no_connection) ImageView noConnection;
    private TextView noFiltersEmptyText;
    private ImageButton fabPosting;
    private GridLayoutManager layoutManager;
    @BindInt(R.integer.num_columns) int columns;
    private boolean connected = true;
    private boolean monitoringConnectivity = false;

    // data
    private DataManager dataManager;
    private FeedAdapter adapter;
    private FilterAdapter filtersAdapter;
    private DesignerNewsPrefs designerNewsPrefs;
    private DribbblePrefs dribbblePrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        drawer.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);

        //toolbar.inflateMenu(R.menu.main);
        setActionBar(toolbar);
        if (savedInstanceState == null) {
            animateToolbar();
        }
        setExitSharedElementCallback(FeedAdapter.createSharedElementReenterCallback(this));

        dribbblePrefs = DribbblePrefs.get(this);
        designerNewsPrefs = DesignerNewsPrefs.get(this);
        filtersAdapter = new FilterAdapter(this, SourceManager.getSources(this),
                new FilterAdapter.FilterAuthoriser() {
            @Override
            public void requestDribbbleAuthorisation(View sharedElement, Source forSource) {
                Intent login = new Intent(HomeActivity.this, DribbbleLogin.class);
                MorphTransform.addExtras(login,
                        ContextCompat.getColor(HomeActivity.this, R.color.background_dark),
                        sharedElement.getHeight() / 2);
                ActivityOptions options =
                        ActivityOptions.makeSceneTransitionAnimation(HomeActivity.this,
                                sharedElement, getString(R.string.transition_dribbble_login));
                startActivityForResult(login,
                        getAuthSourceRequestCode(forSource), options.toBundle());
            }
        });
        dataManager = new DataManager(this, filtersAdapter) {
            @Override
            public void onDataLoaded(List<? extends PlaidItem> data) {
                adapter.addAndResort(data);
                checkEmptyState();
            }
        };
        adapter = new FeedAdapter(this, dataManager, columns, PocketUtils.isPocketInstalled(this));

        grid.setAdapter(adapter);
        layoutManager = new GridLayoutManager(this, columns);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return adapter.getItemColumnSpan(position);
            }
        });
        grid.setLayoutManager(layoutManager);
        grid.addOnScrollListener(toolbarElevation);
        grid.addOnScrollListener(new InfiniteScrollListener(layoutManager, dataManager) {
            @Override
            public void onLoadMore() {
                dataManager.loadAllDataSources();
            }
        });
        grid.setHasFixedSize(true);
        grid.addItemDecoration(new GridItemDividerDecoration(adapter.getDividedViewHolderClasses(),
                this, R.dimen.divider_height, R.color.divider));
        grid.setItemAnimator(new HomeGridItemAnimator());

        // drawer layout treats fitsSystemWindows specially so we have to handle insets ourselves
        drawer.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
            @Override
            public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
                // inset the toolbar down by the status bar height
                ViewGroup.MarginLayoutParams lpToolbar = (ViewGroup.MarginLayoutParams) toolbar
                        .getLayoutParams();
                lpToolbar.topMargin += insets.getSystemWindowInsetTop();
                lpToolbar.leftMargin += insets.getSystemWindowInsetLeft();
                lpToolbar.rightMargin += insets.getSystemWindowInsetRight();
                toolbar.setLayoutParams(lpToolbar);

                // inset the grid top by statusbar+toolbar & the bottom by the navbar (don't clip)
                grid.setPadding(
                        grid.getPaddingLeft() + insets.getSystemWindowInsetLeft(), // landscape
                        insets.getSystemWindowInsetTop() + ViewUtils.getActionBarSize
                                (HomeActivity.this),
                        grid.getPaddingRight() + insets.getSystemWindowInsetRight(), // landscape
                        grid.getPaddingBottom() + insets.getSystemWindowInsetBottom());

                // inset the fab for the navbar
                ViewGroup.MarginLayoutParams lpFab = (ViewGroup.MarginLayoutParams) fab
                        .getLayoutParams();
                lpFab.bottomMargin += insets.getSystemWindowInsetBottom(); // portrait
                lpFab.rightMargin += insets.getSystemWindowInsetRight(); // landscape
                fab.setLayoutParams(lpFab);

                View postingStub = findViewById(R.id.stub_posting_progress);
                ViewGroup.MarginLayoutParams lpPosting =
                        (ViewGroup.MarginLayoutParams) postingStub.getLayoutParams();
                lpPosting.bottomMargin += insets.getSystemWindowInsetBottom(); // portrait
                lpPosting.rightMargin += insets.getSystemWindowInsetRight(); // landscape
                postingStub.setLayoutParams(lpPosting);

                // we place a background behind the status bar to combine with it's semi-transparent
                // color to get the desired appearance.  Set it's height to the status bar height
                View statusBarBackground = findViewById(R.id.status_bar_background);
                FrameLayout.LayoutParams lpStatus = (FrameLayout.LayoutParams)
                        statusBarBackground.getLayoutParams();
                lpStatus.height = insets.getSystemWindowInsetTop();
                statusBarBackground.setLayoutParams(lpStatus);

                // inset the filters list for the status bar / navbar
                // need to set the padding end for landscape case
                final boolean ltr = filtersList.getLayoutDirection() == View.LAYOUT_DIRECTION_LTR;
                filtersList.setPaddingRelative(filtersList.getPaddingStart(),
                        filtersList.getPaddingTop() + insets.getSystemWindowInsetTop(),
                        filtersList.getPaddingEnd() + (ltr ? insets.getSystemWindowInsetRight() :
                                0),
                        filtersList.getPaddingBottom() + insets.getSystemWindowInsetBottom());

                // clear this listener so insets aren't re-applied
                drawer.setOnApplyWindowInsetsListener(null);

                return insets.consumeSystemWindowInsets();
            }
        });
        setupTaskDescription();

        filtersList.setAdapter(filtersAdapter);
        filtersList.setItemAnimator(new FilterAdapter.FilterAnimator());
        filtersAdapter.registerFilterChangedCallback(filtersChangedCallbacks);
        dataManager.loadAllDataSources();
        ItemTouchHelper.Callback callback = new FilterTouchHelperCallback(filtersAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(filtersList);
        checkEmptyState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        dribbblePrefs.addLoginStatusListener(filtersAdapter);
        checkConnectivity();
    }

    @Override
    protected void onPause() {
        dribbblePrefs.removeLoginStatusListener(filtersAdapter);
        if (monitoringConnectivity) {
            final ConnectivityManager connectivityManager
                    = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            connectivityManager.unregisterNetworkCallback(connectivityCallback);
            monitoringConnectivity = false;
        }
        super.onPause();
    }

    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        if (data == null || resultCode != RESULT_OK
                || !data.hasExtra(DribbbleShot.RESULT_EXTRA_SHOT_ID)) return;

        // When reentering, if the shared element is no longer on screen (e.g. after an
        // orientation change) then scroll it into view.
        final long sharedShotId = data.getLongExtra(DribbbleShot.RESULT_EXTRA_SHOT_ID, -1l);
        if (sharedShotId != -1l                                             // returning from a shot
                && adapter.getDataItemCount() > 0                           // grid populated
                && grid.findViewHolderForItemId(sharedShotId) == null) {    // view not attached
            final int position = adapter.getItemPosition(sharedShotId);
            if (position == RecyclerView.NO_POSITION) return;

            // delay the transition until our shared element is on-screen i.e. has been laid out
            postponeEnterTransition();
            grid.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int l, int t, int r, int b,
                                           int oL, int oT, int oR, int oB) {
                    grid.removeOnLayoutChangeListener(this);
                    startPostponedEnterTransition();
                }
            });
            grid.scrollToPosition(position);
            toolbar.setTranslationZ(-1f);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        final MenuItem dribbbleLogin = menu.findItem(R.id.menu_dribbble_login);
        if (dribbbleLogin != null) {
            dribbbleLogin.setTitle(dribbblePrefs.isLoggedIn() ?
                    R.string.dribbble_log_out : R.string.dribbble_login);
        }
        final MenuItem designerNewsLogin = menu.findItem(R.id.menu_designer_news_login);
        if (designerNewsLogin != null) {
            designerNewsLogin.setTitle(designerNewsPrefs.isLoggedIn() ?
                    R.string.designer_news_log_out : R.string.designer_news_login);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_filter:
                drawer.openDrawer(GravityCompat.END);
                return true;
            case R.id.menu_search:
                View searchMenuView = toolbar.findViewById(R.id.menu_search);
                Bundle options = ActivityOptions.makeSceneTransitionAnimation(this, searchMenuView,
                        getString(R.string.transition_search_back)).toBundle();
                startActivityForResult(new Intent(this, SearchActivity.class), RC_SEARCH, options);
                return true;
            case R.id.menu_dribbble_login:
                if (!dribbblePrefs.isLoggedIn()) {
                    dribbblePrefs.login(HomeActivity.this);
                } else {
                    dribbblePrefs.logout();
                    // TODO something better than a toast!!
                    Toast.makeText(getApplicationContext(), R.string.dribbble_logged_out, Toast
                            .LENGTH_SHORT).show();
                }
                return true;
            case R.id.menu_designer_news_login:
                if (!designerNewsPrefs.isLoggedIn()) {
                    startActivity(new Intent(this, DesignerNewsLogin.class));
                } else {
                    designerNewsPrefs.logout();
                    // TODO something better than a toast!!
                    Toast.makeText(getApplicationContext(), R.string.designer_news_logged_out,
                            Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.menu_about:
                startActivity(new Intent(HomeActivity.this, AboutActivity.class),
                        ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RC_SEARCH:
                // reset the search icon which we hid
                View searchMenuView = toolbar.findViewById(R.id.menu_search);
                if (searchMenuView != null) {
                    searchMenuView.setAlpha(1f);
                }
                if (resultCode == SearchActivity.RESULT_CODE_SAVE) {
                    String query = data.getStringExtra(SearchActivity.EXTRA_QUERY);
                    if (TextUtils.isEmpty(query)) return;
                    Source dribbbleSearch = null;
                    Source designerNewsSearch = null;
                    boolean newSource = false;
                    if (data.getBooleanExtra(SearchActivity.EXTRA_SAVE_DRIBBBLE, false)) {
                        dribbbleSearch = new Source.DribbbleSearchSource(query, true);
                        newSource |= filtersAdapter.addFilter(dribbbleSearch);
                    }
                    if (data.getBooleanExtra(SearchActivity.EXTRA_SAVE_DESIGNER_NEWS, false)) {
                        designerNewsSearch = new Source.DesignerNewsSearchSource(query, true);
                        newSource |= filtersAdapter.addFilter(designerNewsSearch);
                    }
                    if (newSource && (dribbbleSearch != null || designerNewsSearch != null)) {
                        highlightNewSources(dribbbleSearch, designerNewsSearch);
                    }
                }
                break;
            case RC_NEW_DESIGNER_NEWS_STORY:
                switch (resultCode) {
                    case PostNewDesignerNewsStory.RESULT_DRAG_DISMISSED:
                        // need to reshow the FAB as there's no shared element transition
                        showFab();
                        unregisterPostStoryResultListener();
                        break;
                    case PostNewDesignerNewsStory.RESULT_POSTING:
                        showPostingProgress();
                        break;
                    default:
                        unregisterPostStoryResultListener();
                        break;
                }
                break;
            case RC_NEW_DESIGNER_NEWS_LOGIN:
                if (resultCode == RESULT_OK) {
                    showFab();
                }
                break;
            case RC_AUTH_DRIBBBLE_FOLLOWING:
                if (resultCode == RESULT_OK) {
                    filtersAdapter.enableFilterByKey(SourceManager.SOURCE_DRIBBBLE_FOLLOWING, this);
                }
                break;
            case RC_AUTH_DRIBBBLE_USER_LIKES:
                if (resultCode == RESULT_OK) {
                    filtersAdapter.enableFilterByKey(
                            SourceManager.SOURCE_DRIBBBLE_USER_LIKES, this);
                }
                break;
            case RC_AUTH_DRIBBBLE_USER_SHOTS:
                if (resultCode == RESULT_OK) {
                    filtersAdapter.enableFilterByKey(
                            SourceManager.SOURCE_DRIBBBLE_USER_SHOTS, this);
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        dataManager.cancelLoading();
        super.onDestroy();
    }

    // listener for notifying adapter when data sources are deactivated
    private FilterAdapter.FiltersChangedCallbacks filtersChangedCallbacks =
            new FilterAdapter.FiltersChangedCallbacks() {
        @Override
        public void onFiltersChanged(Source changedFilter) {
            if (!changedFilter.active) {
                adapter.removeDataSource(changedFilter.key);
            }
            checkEmptyState();
        }

        @Override
        public void onFilterRemoved(Source removed) {
            adapter.removeDataSource(removed.key);
            checkEmptyState();
        }
    };

    private RecyclerView.OnScrollListener toolbarElevation = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            // we want the grid to scroll over the top of the toolbar but for the toolbar items
            // to be clickable when visible. To achieve this we play games with elevation. The
            // toolbar is laid out in front of the grid but when we scroll, we lower it's elevation
            // to allow the content to pass in front (and reset when scrolled to top of the grid)
            if (newState == RecyclerView.SCROLL_STATE_IDLE
                    && layoutManager.findFirstVisibleItemPosition() == 0
                    && layoutManager.findViewByPosition(0).getTop() == grid.getPaddingTop()
                    && toolbar.getTranslationZ() != 0) {
                // at top, reset elevation
                toolbar.setTranslationZ(0f);
            } else if (newState == RecyclerView.SCROLL_STATE_DRAGGING
                    && toolbar.getTranslationZ() != -1f) {
                // grid scrolled, lower toolbar to allow content to pass in front
                toolbar.setTranslationZ(-1f);
            }
        }
    };

    @OnClick(R.id.fab)
    protected void fabClick() {
        if (designerNewsPrefs.isLoggedIn()) {
            Intent intent = new Intent(this, PostNewDesignerNewsStory.class);
            FabTransform.addExtras(intent,
                    ContextCompat.getColor(this, R.color.accent), R.drawable.ic_add_dark);
            intent.putExtra(PostStoryService.EXTRA_BROADCAST_RESULT, true);
            registerPostStoryResultListener();
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, fab,
                    getString(R.string.transition_new_designer_news_post));
            startActivityForResult(intent, RC_NEW_DESIGNER_NEWS_STORY, options.toBundle());
        } else {
            Intent intent = new Intent(this, DesignerNewsLogin.class);
            FabTransform.addExtras(intent,
                    ContextCompat.getColor(this, R.color.accent), R.drawable.ic_add_dark);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, fab,
                    getString(R.string.transition_designer_news_login));
            startActivityForResult(intent, RC_NEW_DESIGNER_NEWS_LOGIN, options.toBundle());
        }
    }

    BroadcastReceiver postStoryResultReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ensurePostingProgressInflated();
            switch (intent.getAction()) {
                case PostStoryService.BROADCAST_ACTION_SUCCESS:
                    // success animation
                    AnimatedVectorDrawable complete =
                            (AnimatedVectorDrawable) getDrawable(R.drawable.avd_upload_complete);
                    fabPosting.setImageDrawable(complete);
                    complete.start();
                    fabPosting.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            fabPosting.setVisibility(View.GONE);
                        }
                    }, 2100); // length of R.drawable.avd_upload_complete

                    // actually add the story to the grid
                    Story newStory = intent.getParcelableExtra(PostStoryService.EXTRA_NEW_STORY);
                    adapter.addAndResort(Arrays.asList(new Story[]{ newStory }));
                    break;
                case PostStoryService.BROADCAST_ACTION_FAILURE:
                    // failure animation
                    AnimatedVectorDrawable failed =
                            (AnimatedVectorDrawable) getDrawable(R.drawable.avd_upload_error);
                    fabPosting.setImageDrawable(failed);
                    failed.start();
                    // remove the upload progress 'fab' and reshow the regular one
                    fabPosting.animate()
                            .alpha(0f)
                            .rotation(90f)
                            .setStartDelay(2000L) // leave error on screen briefly
                            .setDuration(300L)
                            .setInterpolator(AnimUtils.getFastOutSlowInInterpolator(HomeActivity
                                    .this))
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    fabPosting.setVisibility(View.GONE);
                                    fabPosting.setAlpha(1f);
                                    fabPosting.setRotation(0f);
                                }
                            });
                    break;
            }
            unregisterPostStoryResultListener();
        }
    };

    private void registerPostStoryResultListener() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PostStoryService.BROADCAST_ACTION_SUCCESS);
        intentFilter.addAction(PostStoryService.BROADCAST_ACTION_FAILURE);
        LocalBroadcastManager.getInstance(this).
                registerReceiver(postStoryResultReceiver, intentFilter);
    }

    private void unregisterPostStoryResultListener() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(postStoryResultReceiver);
    }

    private void showPostingProgress() {
        ensurePostingProgressInflated();
        fabPosting.setVisibility(View.VISIBLE);
        // if stub has just been inflated then it will not have been laid out yet
        if (fabPosting.isLaidOut()) {
            revealPostingProgress();
        } else {
            fabPosting.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int l, int t, int r, int b,
                                           int oldL, int oldT, int oldR, int oldB) {
                    fabPosting.removeOnLayoutChangeListener(this);
                    revealPostingProgress();
                }
            });
        }
    }

    private void revealPostingProgress() {
        Animator reveal = ViewAnimationUtils.createCircularReveal(fabPosting,
                (int) fabPosting.getPivotX(),
                (int) fabPosting.getPivotY(),
                0f,
                fabPosting.getWidth() / 2)
                .setDuration(600L);
        reveal.setInterpolator(AnimUtils.getFastOutLinearInInterpolator(this));
        reveal.start();
        AnimatedVectorDrawable uploading =
                (AnimatedVectorDrawable) getDrawable(R.drawable.avd_uploading);
        fabPosting.setImageDrawable(uploading);
        uploading.start();
    }

    private void ensurePostingProgressInflated() {
        if (fabPosting != null) return;
        fabPosting = (ImageButton) ((ViewStub) findViewById(R.id.stub_posting_progress)).inflate();
    }

    private void checkEmptyState() {
        if (adapter.getDataItemCount() == 0) {
            // if grid is empty check whether we're loading or if no filters are selected
            if (filtersAdapter.getEnabledSourcesCount() > 0) {
                if (connected) {
                    loading.setVisibility(View.VISIBLE);
                    setNoFiltersEmptyTextVisibility(View.GONE);
                }
            } else {
                loading.setVisibility(View.GONE);
                setNoFiltersEmptyTextVisibility(View.VISIBLE);
            }
            toolbar.setTranslationZ(0f);
        } else {
            loading.setVisibility(View.GONE);
            setNoFiltersEmptyTextVisibility(View.GONE);
        }
    }

    private void setNoFiltersEmptyTextVisibility(int visibility) {
        if (visibility == View.VISIBLE) {
            if (noFiltersEmptyText == null) {
                // create the no filters empty text
                ViewStub stub = (ViewStub) findViewById(R.id.stub_no_filters);
                noFiltersEmptyText = (TextView) stub.inflate();
                String emptyText = getString(R.string.no_filters_selected);
                int filterPlaceholderStart = emptyText.indexOf('\u08B4');
                int altMethodStart = filterPlaceholderStart + 3;
                SpannableStringBuilder ssb = new SpannableStringBuilder(emptyText);
                // show an image of the filter icon
                ssb.setSpan(new ImageSpan(this, R.drawable.ic_filter_small,
                                ImageSpan.ALIGN_BASELINE),
                        filterPlaceholderStart,
                        filterPlaceholderStart + 1,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                // make the alt method (swipe from right) less prominent and italic
                ssb.setSpan(new ForegroundColorSpan(
                                ContextCompat.getColor(this, R.color.text_secondary_light)),
                        altMethodStart,
                        emptyText.length(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ssb.setSpan(new StyleSpan(Typeface.ITALIC),
                        altMethodStart,
                        emptyText.length(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                noFiltersEmptyText.setText(ssb);
                noFiltersEmptyText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        drawer.openDrawer(GravityCompat.END);
                    }
                });
            }
            noFiltersEmptyText.setVisibility(visibility);
        } else if (noFiltersEmptyText != null) {
            noFiltersEmptyText.setVisibility(visibility);
        }

    }

    private void setupTaskDescription() {
        // set a silhouette icon in overview as the launcher icon is a bit busy
        // and looks bad on top of colorPrimary
        //Bitmap overviewIcon = ImageUtils.vectorToBitmap(this, R.drawable.ic_launcher_silhouette);
        // TODO replace launcher icon with a monochrome version from RN.
        Bitmap overviewIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        setTaskDescription(new ActivityManager.TaskDescription(getString(R.string.app_name),
                overviewIcon,
                ContextCompat.getColor(this, R.color.primary)));
        overviewIcon.recycle();
    }

    private void animateToolbar() {
        // this is gross but toolbar doesn't expose it's children to animate them :(
        View t = toolbar.getChildAt(0);
        if (t != null && t instanceof TextView) {
            TextView title = (TextView) t;

            // fade in and space out the title.  Animating the letterSpacing performs horribly so
            // fake it by setting the desired letterSpacing then animating the scaleX ¯\_(ツ)_/¯
            title.setAlpha(0f);
            title.setScaleX(0.8f);

            title.animate()
                    .alpha(1f)
                    .scaleX(1f)
                    .setStartDelay(300)
                    .setDuration(900)
                    .setInterpolator(AnimUtils.getFastOutSlowInInterpolator(this));
        }
        View amv = toolbar.getChildAt(1);
        if (amv != null & amv instanceof ActionMenuView) {
            ActionMenuView actions = (ActionMenuView) amv;
            popAnim(actions.getChildAt(0), 500, 200); // filter
            popAnim(actions.getChildAt(1), 700, 200); // overflow
        }
    }

    private void popAnim(View v, int startDelay, int duration) {
        if (v != null) {
            v.setAlpha(0f);
            v.setScaleX(0f);
            v.setScaleY(0f);

            v.animate()
                    .alpha(1f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .setStartDelay(startDelay)
                    .setDuration(duration)
                    .setInterpolator(AnimationUtils.loadInterpolator(this,
                            android.R.interpolator.overshoot));
        }
    }

    private void showFab() {
        fab.setAlpha(0f);
        fab.setScaleX(0f);
        fab.setScaleY(0f);
        fab.setTranslationY(fab.getHeight() / 2);
        fab.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .translationY(0f)
                .setDuration(300L)
                .setInterpolator(AnimUtils.getLinearOutSlowInInterpolator(this))
                .start();
    }

    /**
     * Highlight the new source(s) by:
     *      1. opening the drawer
     *      2. scrolling new source(s) into view
     *      3. flashing new source(s) background
     *      4. closing the drawer (if user hasn't interacted with it)
     */
    private void highlightNewSources(final Source... sources) {
        final Runnable closeDrawerRunnable = new Runnable() {
            @Override
            public void run() {
                drawer.closeDrawer(GravityCompat.END);
            }
        };
        drawer.setDrawerListener(new DrawerLayout.SimpleDrawerListener() {

            // if the user interacts with the filters while it's open then don't auto-close
            private final View.OnTouchListener filtersTouch = new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    drawer.removeCallbacks(closeDrawerRunnable);
                    return false;
                }
            };

            @Override
            public void onDrawerOpened(View drawerView) {
                // scroll to the new item(s) and highlight them
                List<Integer> filterPositions = new ArrayList<>(sources.length);
                for (Source source : sources) {
                    if (source != null) {
                        filterPositions.add(filtersAdapter.getFilterPosition(source));
                    }
                }
                int scrollTo = Collections.max(filterPositions);
                filtersList.smoothScrollToPosition(scrollTo);
                for (int position : filterPositions) {
                    filtersAdapter.highlightFilter(position);
                }
                filtersList.setOnTouchListener(filtersTouch);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                // reset
                filtersList.setOnTouchListener(null);
                drawer.setDrawerListener(null);
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                // if the user interacts with the drawer manually then don't auto-close
                if (newState == DrawerLayout.STATE_DRAGGING) {
                    drawer.removeCallbacks(closeDrawerRunnable);
                }
            }
        });
        drawer.openDrawer(GravityCompat.END);
        drawer.postDelayed(closeDrawerRunnable, 2000L);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
        }
    }

    private void checkConnectivity() {
        final ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        connected = activeNetworkInfo != null && activeNetworkInfo.isConnected();
        if (!connected) {
            loading.setVisibility(View.GONE);
            if (noConnection == null) {
                final ViewStub stub = (ViewStub) findViewById(R.id.stub_no_connection);
                noConnection = (ImageView) stub.inflate();
            }
            final AnimatedVectorDrawable avd =
                    (AnimatedVectorDrawable) getDrawable(R.drawable.avd_no_connection);
            noConnection.setImageDrawable(avd);
            avd.start();

            connectivityManager.registerNetworkCallback(
                    new NetworkRequest.Builder()
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).build(),
                    connectivityCallback);
            monitoringConnectivity = true;
        }
    }

    private ConnectivityManager.NetworkCallback connectivityCallback = new ConnectivityManager.NetworkCallback() {
        @Override
        public void onAvailable(Network network) {
            connected = true;
            if (adapter.getDataItemCount() != 0) return;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TransitionManager.beginDelayedTransition(drawer);
                    noConnection.setVisibility(View.GONE);
                    loading.setVisibility(View.VISIBLE);
                    dataManager.loadAllDataSources();
                }
            });
        }

        @Override
        public void onLost(Network network) {
            connected = false;
        }
    };

    private int getAuthSourceRequestCode(Source filter) {
        switch (filter.key) {
            case SourceManager.SOURCE_DRIBBBLE_FOLLOWING:
                return RC_AUTH_DRIBBBLE_FOLLOWING;
            case SourceManager.SOURCE_DRIBBBLE_USER_LIKES:
                return RC_AUTH_DRIBBBLE_USER_LIKES;
            case SourceManager.SOURCE_DRIBBBLE_USER_SHOTS:
                return RC_AUTH_DRIBBBLE_USER_SHOTS;
        }
        throw new InvalidParameterException();
    }
}
