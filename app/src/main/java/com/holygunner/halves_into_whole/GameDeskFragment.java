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

import static com.holygunner.halves_into_whole.game_mechanics.StepResult.*;

public class GameDeskFragment extends Fragment {

    private RecyclerView mRecyclerGridDesk;
    private RecyclerGridAdapter mAdapter;

    private Button turnFigureButton;
    private TextView gamerCountView;
    private TextView levelNameTextView;

    private RelativeLayout parentLayout;
    private RelativeLayout gameOverLayout;
    private TextView warningTextView;

    private boolean userActionAvailable;
    private boolean isTurnButtonClickable;

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
        mGameManager.startOrResumeGame(getActivity().getIntent().getIntExtra(
                StartGameActivity.OPEN_LEVEL_NUMB_KEY, 0));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.activity_game, container, false);
        parentLayout = view.findViewById(R.id.parentLayout);

        warningTextView = view.findViewById(R.id.warningTextView);
        gameOverLayout = view.findViewById(R.id.gameOverLayout);

        mRecyclerGridDesk = view.findViewById(R.id.recycler_grid_game_desk);
        mRecyclerGridDesk.setLayoutManager(new GridLayoutManager(getActivity(),
                mGameManager.getGamePlay().getLevel().getDeskSize()[1]));

        turnFigureButton = view.findViewById(R.id.turnFigureButton);
        gamerCountView = view.findViewById(R.id.gamerCountTextView);
        levelNameTextView = view.findViewById(R.id.levelNameTextView);

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
        mInterstitialAd = new InterstitialAd(getContext());
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
        userActionAvailable = true;
        if (!mGameManager.getGamePlay().isGameContinue()){
            userActionAvailable = false;
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

        levelNameTextView.setText(levelName);

        String title;

        if (!LevelsValues.isEndlessMode(levelNumb)){
            title = gamerCount
                    + getString(R.string.count_delimiter)
                    + levelRounds;
            gamerCountView.setText(title);
        }   else {
            title = gamerCount
                    + getString(R.string.count_delimiter)
                    + getResources().getString(R.string.infinity_symbol);
            gamerCountView.setText(title);
        }
    }

    private void gameOver(){
        String gameOver = getResources().getString(R.string.game_over);
        prepareViewsForFinish(gameOver);

        mSoundPoolWrapper.playSound(SoundPoolWrapper.LEVEL_LOSE);

        gameOverLayout.setOnClickListener(new View.OnClickListener() {
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
        levelNameTextView.setVisibility(View.INVISIBLE);
        prepareViewsForFinish(nextLevelStr);

        mSoundPoolWrapper.playSound(SoundPoolWrapper.LEVEL_COMPLETE);

        gameOverLayout.setOnClickListener(new View.OnClickListener() {
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
        parentLayout.setAlpha(0.4f);

        for (int i = 0; i < parentLayout.getChildCount(); i++) {
            View child = parentLayout.getChildAt(i);
            child.setEnabled(false);
        }

        warningTextView.setText(warningText);
        warningTextView.setVisibility(View.VISIBLE);
        animateWarningTextView(warningTextView,0.0f,150);
    }

    private void showBonusWarning(String text){
        long duration = 150;
        float alphaStart = 0.2f;
        float alphaEnd = 1.0f;
        warningTextView.setText(text);
        warningTextView.setAlpha(alphaStart);
        warningTextView.setVisibility(View.VISIBLE);
        animateWarningTextView(warningTextView,alphaEnd,duration);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                warningTextView.setVisibility(View.INVISIBLE);
            }
        }, duration);
    }

    private void finishActivity(){
        showAdsInterstitial();
        mGameManager.finish();
        getActivity().finish();
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

        @Override
        public GridViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mLayoutInflater.inflate(R.layout.list_item_cell, parent, false);
            return new GridViewHolder(view);
        }

        @Override
        public void onBindViewHolder(GridViewHolder holder, int position) {
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

                    if (!userActionAvailable){
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

                                if (isTurnButtonClickable) {
                                    setIsTurnButtonClickable(true);
                                }
                                setTurnFigureButton();
                            }
                        }
                }
            });
        }

        private void setTurnFigureButton(){
            turnFigureButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isTurnButtonClickable) {
                        if (mGameManager.getGamePlay().turnFigureIfExists(position)) {
                            setIsTurnButtonClickable(false);
                            isTurnButtonClickable = false;
                            turnFigure(mImageViewCell);
                        }
                    }
                }
            });
        }

        private void showUnitedFigure(ImageView imageViewCell, GamePlay gamePlay){
            userActionAvailable = false;
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
            userActionAvailable = false;

            mSoundPoolWrapper.playSound(SoundPoolWrapper.TURN_FIGURE);

            imageView.animate().rotation(90).setDuration(DELAY).start();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    updateFillCells();
                    userActionAvailable = true;

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
            userActionAvailable = false;
            fillCells(false, Color.TRANSPARENT);
            setImageViewRes(mGameManager.getGamePlay().getRecentPosition(), R.drawable.empty_cell);
            setImageViewRes(currentPosition, FigureFactory.getFigureRes(mGameManager.getDesk().getFigure(currentPosition)));
            showRecentRandomFiguresWithDelay();
        }

        private void setImageViewRes(int position, int res){
            GridViewHolder holder = (GridViewHolder) mRecyclerGridDesk.findViewHolderForAdapterPosition(position);
            ImageView imageView = (ImageView) holder.mImageViewCell.findViewById(R.id.cell_image_view);
            imageView.setImageResource(res);
        }

        private void updateFillCells(){
            Figure recentFigure =  mGameManager.getDesk().getFigure(mGameManager.getGamePlay().getRecentPosition());

        if (!mGameManager.getGamePlay().getRecentRandomFigures().isEmpty()){
            Figure addedRandomFigure = mGameManager.getGamePlay().getRecentRandomFigures().get(0);
            setImageViewRes(mGameManager.getDesk().cellToPosition(addedRandomFigure.mCell), FigureFactory.getFigureRes(addedRandomFigure));
        }

            int currentFigureColor = recentFigure.color;
            fillCells(true, currentFigureColor);
        }

        private void fillCells(boolean isFill, int currentFigureColor){
            int color = getCellsColor(isFill);
            Desk desk = mGameManager.getDesk();
            AvailableSteps availableSteps = mGameManager.getGamePlay().getAvailableSteps();

            for (int y = 0; y<desk.deskToMultiArr().length; y++){
                for (int x = 0; x<desk.deskToMultiArr()[y].length; x++){
                    Cell cell = new Cell(x, y);
                    int position = mGameManager.getDesk().cellToPosition(cell);

                    if (availableSteps.getAvailableToStepCells().contains(cell)){
                        setBackgroundColorOnPosition(position, color);
                    }   else
                    if (availableSteps.getAvailableToUniteCells().contains(cell)) {
                        setBackgroundColorOnPosition(position, currentFigureColor);
                    }   else {
                        setBackgroundColorOnPosition(position, Color.TRANSPARENT);
                    }
                }
            }
            setBackgroundColorOnPosition(mGameManager.getGamePlay().getRecentPosition(), currentFigureColor);
        }

        private void setBackgroundColorOnPosition(int position, int color){
            mRecyclerGridDesk.getLayoutManager().findViewByPosition(position).setBackgroundColor(color);
        }

        private int getCellsColor(boolean isFillColor){
            int color;

            if (isFillColor) {
                color = ContextCompat.getColor(getContext(), R.color.currentFigureFill);
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
                        int position = mGameManager.getDesk().cellToPosition(figure.mCell);
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
        isTurnButtonClickable = isClickable;
        setIsTurnButtonVisible(isClickable);
    }

    private void setIsTurnButtonVisible(boolean isVisible){
        if (isVisible){
            turnFigureButton.setVisibility(View.VISIBLE);
        }   else {
            turnFigureButton.setVisibility(View.INVISIBLE);
        }
    }
}