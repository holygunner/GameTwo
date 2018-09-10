package com.holygunner.halves_into_whole;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.holygunner.halves_into_whole.figures.Figure;
import com.holygunner.halves_into_whole.figures.FigureFactory;
import com.holygunner.halves_into_whole.game_mechanics.*;
import com.holygunner.halves_into_whole.sound.SoundPoolWrapper;
import com.holygunner.halves_into_whole.values.LevelsValues;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;
import java.util.Objects;

import static com.holygunner.halves_into_whole.game_mechanics.StepResult.*;

public class GameDeskFragment extends Fragment {

    private RecyclerView mRecyclerGridDesk;
    private RecyclerGridAdapter mAdapter;
    private Button mTurnFigureButton;
    private TextView mGamerCountView;
    private TextView mLevelNameTextView;
    private RelativeLayout mParentLayout;
    private RelativeLayout mGameOverLayout;
    private TextView mWarningTextView;

    private boolean mUserActionAvailable;
    private boolean mIsTurnButtonClickable;

    private GameManager mGameManager;
    private SoundPoolWrapper mSoundPoolWrapper;

    private InterstitialAd mInterstitialAd;

    private Handler mHandler;
    private final long DELAY = 150;

    public GameDeskFragment(){
    }

    public static GameDeskFragment newInstance(){
        return new GameDeskFragment();
    }

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mSoundPoolWrapper = SoundPoolWrapper.getInstance(getActivity());

        // Sample AdMob app ID: ca-app-pub-3940256099942544~3347511713
        // my AdMob ID: ca-app-pub-5986847491806907~4171322067
        MobileAds.initialize(getContext(), "ca-app-pub-5986847491806907~4171322067");
        setAdsInterstitial();

        initGameManager();
        mGameManager.startOrResumeGame(Objects.requireNonNull(getActivity()).getIntent().getIntExtra(
                StartGameActivity.OPEN_LEVEL_NUMB_KEY, 0));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.activity_game, container, false);
        mParentLayout = view.findViewById(R.id.parentLayout);

        mWarningTextView = view.findViewById(R.id.warningTextView);
        mGameOverLayout = view.findViewById(R.id.gameOverLayout);

        mRecyclerGridDesk = view.findViewById(R.id.recycler_grid_game_desk);
        mRecyclerGridDesk.setLayoutManager(new GridLayoutManager(getActivity(),
                mGameManager.getGamePlay().getLevel().getDeskSize()[1]));

        mTurnFigureButton = view.findViewById(R.id.turnFigureButton);
        mGamerCountView = view.findViewById(R.id.gamerCountTextView);
        mLevelNameTextView = view.findViewById(R.id.levelNameTextView);

        updateGamerCount(true);

        return view;
    }

    @Override
    public void onResume(){
        super.onResume();

        setIsTurnButtonClickable(mGameManager.getSaver().readIsTurnButtonClickable());
        updateRecyclerGridDesk();
    }

    @Override
    public void onPause(){
        super.onPause();
        boolean isSaved = mGameManager.save();
        Log.i("TAG", "save is succesful: " + isSaved);
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        finishActivity();
    }

    private void setAdsInterstitial(){
        mInterstitialAd = new InterstitialAd(Objects.requireNonNull(getContext()));
        // Sample AdMob app ID: ca-app-pub-3940256099942544/1033173712
        // my app ID: ca-app-pub-5986847491806907/4143437299
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed(){
                // Load the next interstitial.
                mInterstitialAd.loadAd(new AdRequest.Builder()
                        .addTestDevice("77E6E08ABF5409D1A37C98C74EE45A35") // delete before app release
                        .build());
            }
        });
    }

    private void showAdsInterstitial(){
        if (mGameManager.getGamePlay().getLevel().isLevelComplete()
                || !(mGameManager.getGamePlay().isGameContinue())) {
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            } else {
                Log.d("TAG", "The interstitial wasn't loaded yet.");
            }
        }
    }

    private void initGameManager(){
        if (mGameManager == null){
            mGameManager = new GameManager(getActivity());
        }
    }

    private void updateRecyclerGridDesk(){
        List<String> data = DeskToCellsListConverter.getCellList(mGameManager.getDesk());
        mAdapter = new RecyclerGridAdapter(getActivity(), data);
        mRecyclerGridDesk.setAdapter(mAdapter);

        updateGamerCount(false);
        mUserActionAvailable = true;
        if (!mGameManager.getGamePlay().isGameContinue()){
            mUserActionAvailable = false;
            gameOver();
        }
    }

    private void updateGamerCount(boolean isReadGamerCount){
        int gamerCount;
        int levelRounds = 0;
        int levelNumb = mGameManager.getGamePlay().getLevel().getLevelNumb();
        String levelName = mGameManager.getGamePlay().getLevel().getLevelName();

        if (isReadGamerCount){
            gamerCount = 0;
        }   else {
            gamerCount = mGameManager.getGamePlay().getLevel().getGamerCount();
            levelRounds = mGameManager.getGamePlay().getLevel().getLevelRounds();
        }

        mLevelNameTextView.setText(levelName);

        String title;

        if (LevelsValues.isEndlessMode(levelNumb)){
            title = gamerCount
                    + getString(R.string.count_delimiter)
                    + levelRounds;
            mGamerCountView.setText(title);
        }   else {
            title = gamerCount
                    + getString(R.string.count_delimiter)
                    + getResources().getString(R.string.infinity_symbol);
            mGamerCountView.setText(title);
        }
    }

    private void gameOver(){
        String gameOver = getResources().getString(R.string.game_over);
        prepareViewsForFinish(gameOver);

        mSoundPoolWrapper.playSound(SoundPoolWrapper.LEVEL_LOSE);

        mGameOverLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishActivity();
            }
        });
    }

    private void goingToNextLevel(){
        mGameManager.getGamePlay().increaseLevelNumb();
        int nextLevelNumb = mGameManager.getGamePlay().getLevelNumb();

        String nextLevelStr = LevelsValues.LEVELS_NAMES[nextLevelNumb];
        mLevelNameTextView.setVisibility(View.INVISIBLE);
        prepareViewsForFinish(nextLevelStr);

        mSoundPoolWrapper.playSound(SoundPoolWrapper.LEVEL_COMPLETE);

        mGameOverLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), GameFragmentActivity.class);
                intent.putExtra(StartGameActivity.OPEN_LEVEL_NUMB_KEY, mGameManager.getGamePlay().getLevelNumb());
                startActivity(intent);
                finishActivity();
            }
        });
    }

    private void prepareViewsForFinish(String warningText){
        mParentLayout.setAlpha(0.4f);

        for (int i = 0; i < mParentLayout.getChildCount(); i++) {
            View child = mParentLayout.getChildAt(i);
            child.setEnabled(false);
        }

        mWarningTextView.setText(warningText);
        mWarningTextView.setVisibility(View.VISIBLE);
        animateWarningTextView(mWarningTextView,0.0f,150);
    }

    private void showBonusWarning(String text){
        long duration = 150;
        float alphaStart = 0.2f;
        float alphaEnd = 1.0f;
        mWarningTextView.setText(text);
        mWarningTextView.setAlpha(alphaStart);
        mWarningTextView.setVisibility(View.VISIBLE);
        mSoundPoolWrapper.playSound(SoundPoolWrapper.COMBO);
        animateWarningTextView(mWarningTextView,alphaEnd,duration);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mWarningTextView.setVisibility(View.INVISIBLE);
            }
        }, duration);
    }

    private void finishActivity(){
        showAdsInterstitial();
        mGameManager.finish();
        Objects.requireNonNull(getActivity()).finish();
    }

    private void animateWarningTextView(final TextView view, float alpha, long duration){
        view.animate()
                .alpha(alpha)
                .setDuration(duration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                    }
                });
    }

    private class RecyclerGridAdapter extends RecyclerView.Adapter<GridViewHolder> {
        private List<String> mData;
        private LayoutInflater mLayoutInflater;

        RecyclerGridAdapter(Context context, List<String> data){
            mLayoutInflater = LayoutInflater.from(context);
            mData = data;
        }

        @NonNull
        @Override
        public GridViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = mLayoutInflater.inflate(R.layout.list_item_cell, parent, false);
            return new GridViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull GridViewHolder holder, int position) {
            setImage(position, holder);
            holder.setPosition(position);
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        private void setImage(int position, GridViewHolder holder){
            Cell cell = mGameManager.getDesk().positionToCell(position);

            Figure figure = mGameManager.getDesk().getFigure(cell);
            int res;

            if (figure != null){
                res = FigureFactory.getFigureRes(figure);
                holder.mImageViewCell.setImageResource(res);
            }   else {
                res = R.drawable.empty_cell;
                holder.mImageViewCell.setImageResource(res);
            }
        }
    }

    private class GridViewHolder extends RecyclerView.ViewHolder{
        public ImageView mImageViewCell;
        private int position;

        GridViewHolder(View itemView) {
            super(itemView);
            mImageViewCell = itemView.findViewById(R.id.cell_image_view);
            setIsTurnButtonVisible(false);

            mImageViewCell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final GamePlay gamePlay = mGameManager.getGamePlay();

                    if (!mUserActionAvailable){
                        return;
                    }
                    actionDown(gamePlay);
                }

                private void actionDown(GamePlay gamePlay){
                    StepResult stepResult;

                    if ((stepResult = gamePlay.tryToStep(position)) != STEP_UNAVAILABLE){
                        setIsTurnButtonClickable(true);
                        setIsTurnButtonVisible(false);

                        switch (stepResult){
                            case UNITE_FIGURE:
                                showUnitedFigure(mImageViewCell, gamePlay);
                                break;
                            case COMBO:
                                showUnitedFigure(mImageViewCell, gamePlay);
                                showBonusWarning("COMBO! +5");
                                break;
                            case DESK_EMPTY:
                                showUnitedFigure(mImageViewCell, gamePlay);
                                showBonusWarning("BONUS! +10");
                                break;
                            case LEVEL_COMPLETE:
                                showUnitedFigure(mImageViewCell, gamePlay);
                                goingToNextLevel();
                                return;
                            default:
                                replaceFigure(position);
                                break;
                        }
                    }   else {
                            boolean isFilled = gamePlay.setAvailableCells(position);
                            if (isFilled){
                                int currentFigureColor = mGameManager.getDesk().getFigure(position).color;

                                mSoundPoolWrapper.playSound(SoundPoolWrapper.SELECT_FIGURE);
                                fillCells(true, currentFigureColor);

                                if (mIsTurnButtonClickable) {
                                    setIsTurnButtonClickable(true);
                                }
                                setTurnFigureButton();
                            }
                        }
                }
            });
        }

        private void setTurnFigureButton(){
            mTurnFigureButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mIsTurnButtonClickable) {
                        if (mGameManager.getGamePlay().turnFigureIfExists(position)) {
                            setIsTurnButtonClickable(false);
                            mIsTurnButtonClickable = false;
                            turnFigure(mImageViewCell);
                        }
                    }
                }
            });
        }

        private void showUnitedFigure(ImageView imageViewCell, GamePlay gamePlay){
            mUserActionAvailable = false;
            final Handler HANDLER = new Handler();

            mSoundPoolWrapper.playSound(SoundPoolWrapper.UNITE_FIGURE);
            setImageViewRes(gamePlay.getRecentPosition(), R.drawable.empty_cell);
            imageViewCell.setImageResource(gamePlay.getLastUnitedFigureRes());

            fillCells(false, Color.TRANSPARENT);

            HANDLER.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showRecentRandomFiguresWithDelay();
                }
                }, DELAY);
        }

        public void setPosition(int position) {
            this.position = position;
        }

        private void turnFigure(ImageView imageView){
            mUserActionAvailable = false;

            mSoundPoolWrapper.playSound(SoundPoolWrapper.TURN_FIGURE);

            imageView.animate().rotation(90).setDuration(DELAY).start();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    updateFillCells();
                    mUserActionAvailable = true;

                    if (!mGameManager.getGamePlay().isGameContinue()){
                        updateRecyclerGridDesk();
                    }
                }
            }, DELAY);
        }

        private void replaceFigure(int currentPosition){
            if (mRecyclerGridDesk == null){
                return;
            }

            List<Figure> recentRandFigures = mGameManager.getGamePlay().getRecentRandomFigures();

            if (recentRandFigures.isEmpty()){
                return;
            }

            mSoundPoolWrapper.playSound(SoundPoolWrapper.REPLACE_FIGURE);
            mUserActionAvailable = false;
            fillCells(false, Color.TRANSPARENT);
            setImageViewRes(mGameManager.getGamePlay().getRecentPosition(), R.drawable.empty_cell);
            setImageViewRes(currentPosition, FigureFactory.getFigureRes(mGameManager.getDesk().getFigure(currentPosition)));
            showRecentRandomFiguresWithDelay();
        }

        private void setImageViewRes(int position, int res){
            GridViewHolder holder = (GridViewHolder) mRecyclerGridDesk.findViewHolderForAdapterPosition(position);
            ImageView imageView = holder.mImageViewCell.findViewById(R.id.cell_image_view);
            imageView.setImageResource(res);
        }

        private void updateFillCells(){
            Figure recentFigure =  mGameManager.getDesk().getFigure(mGameManager.getGamePlay().getRecentPosition());

        if (!mGameManager.getGamePlay().getRecentRandomFigures().isEmpty()){
            Figure addedRandomFigure = mGameManager.getGamePlay().getRecentRandomFigures().get(0);
            setImageViewRes(mGameManager.getDesk().cellToPosition(addedRandomFigure.cell), FigureFactory.getFigureRes(addedRandomFigure));
        }

            int currentFigureColor = recentFigure.color;
            fillCells(true, currentFigureColor);
        }

        private void fillCells(boolean isFill, int currFigureColor){
            int color = getCellsColor(isFill);
            Desk desk = mGameManager.getDesk();
            AvailableSteps availableSteps = mGameManager.getGamePlay().getAvailableSteps();
            int currFigureColorAlpha = adjustAlpha(currFigureColor);

            for (int y = 0; y<desk.deskToMultiArr().length; y++){
                for (int x = 0; x<desk.deskToMultiArr()[y].length; x++){
                    Cell cell = new Cell(x, y);
                    int position = mGameManager.getDesk().cellToPosition(cell);

                    if (availableSteps.getAvailableToStepCells().contains(cell)){
                        setBackgroundColorOnPosition(position, color);
                    }   else
                    if (availableSteps.getAvailableToUniteCells().contains(cell)) {
                        setBackgroundColorOnPosition(position, currFigureColorAlpha);
                    }   else {
                        setBackgroundColorOnPosition(position, Color.TRANSPARENT);
                    }
                }
            }
            setBackgroundColorOnPosition(mGameManager.getGamePlay().getRecentPosition(), currFigureColor);
        }

        @ColorInt
        private int adjustAlpha(@ColorInt int color) {
            float factor = 0.8f;
            int alpha = Math.round(Color.alpha(color) * factor);
            int red = Color.red(color);
            int green = Color.green(color);
            int blue = Color.blue(color);
            return Color.argb(alpha, red, green, blue);
        }

        private void setBackgroundColorOnPosition(int position, int color){
            mRecyclerGridDesk.getLayoutManager().findViewByPosition(position).setBackgroundColor(color);
        }

        private int getCellsColor(boolean isFillColor){
            int color;

            if (isFillColor) {
                color = ContextCompat.getColor(Objects.requireNonNull(getContext()), R.color.currentFigureFill);
                mGameManager.getGamePlay().setIsFilled(true);
            }   else {
                color = Color.TRANSPARENT;
                mGameManager.getGamePlay().setIsFilled(false);
            }
            return color;
        }

        @SuppressLint("HandlerLeak")
        private void showRecentRandomFiguresWithDelay(){
            final List<Figure> RECENT_RAND_FIGURES = mGameManager.getGamePlay().getRecentRandomFigures();
            final long DELAY = 100;

            mHandler = new Handler() {
                int indx = RECENT_RAND_FIGURES.size() - 1;

                public void handleMessage(Message msg) {
                    super.handleMessage(msg);

                    if (indx > -1) {
                        mSoundPoolWrapper.playSound(SoundPoolWrapper.APPEAR_FIGURE);
                        Figure figure = RECENT_RAND_FIGURES.get(indx);
                        int position = mGameManager.getDesk().cellToPosition(figure.cell);
                        mAdapter.notifyItemChanged(position);
                    }
                    --indx;
                    this.sendEmptyMessageDelayed(0, DELAY);
                }
            };
            mHandler.sendEmptyMessage(0);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    updateRecyclerGridDesk();
                }
            }, DELAY*(RECENT_RAND_FIGURES.size()));
        }
    }

    private void setIsTurnButtonClickable(boolean isClickable){
        mGameManager.getGamePlay().setTurnAvailable(isClickable);
        mIsTurnButtonClickable = isClickable;
        setIsTurnButtonVisible(isClickable);
    }

    private void setIsTurnButtonVisible(boolean isVisible){
        if (isVisible){
            mTurnFigureButton.setVisibility(View.VISIBLE);
        }   else {
            mTurnFigureButton.setVisibility(View.INVISIBLE);
        }
    }
}