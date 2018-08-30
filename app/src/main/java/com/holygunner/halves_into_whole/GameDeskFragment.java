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
        MobileAds.initialize(getContext(), "ca-app-pub-3940256099942544~3347511713");
        setAdsInterstitial();

        initGameManager();
        mGameManager.startOrResumeGame(getActivity().getIntent().getIntExtra(
                StartGameActivity.OPEN_LEVEL_NUMB_KEY, 0));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.activity_game, container, false);
        parentLayout = (RelativeLayout) view.findViewById(R.id.parentLayout);

        warningTextView = (TextView) view.findViewById(R.id.warningTextView);
        gameOverLayout = (RelativeLayout) view.findViewById(R.id.gameOverLayout);

        mRecyclerGridDesk = (RecyclerView) view.findViewById(R.id.recycler_grid_game_desk);
        mRecyclerGridDesk.setLayoutManager(new GridLayoutManager(getActivity(),
                mGameManager.getGamePlay().getLevel().getDeskSize()[1]));

        turnFigureButton = (Button) view.findViewById(R.id.turnFigureButton);
        gamerCountView = (TextView) view.findViewById(R.id.gamerCountTextView);
        levelNameTextView = (TextView) view.findViewById(R.id.levelNameTextView);

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
        mGameManager.save();
        finishActivity();
    }

    private void setAdsInterstitial(){
        mInterstitialAd = new InterstitialAd(getContext());
        // Sample AdMob app ID: ca-app-pub-3940256099942544/1033173712
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed(){
                // Load the next interstitial.
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
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

        if (!LevelsValues.isEndlessMode(levelNumb)){
            gamerCountView.setText(gamerCount + " : " + levelRounds);
        }   else {
            gamerCountView.setText(gamerCount + " : " + getResources().getString(R.string.infinity_symbol));
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

        String nextLevelStr = new String(LevelsValues.LEVELS_NAMES[nextLevelNumb]);
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
        animateGameOver(warningTextView,0.0f,150);
    }

    private void showBonusWarning(String text){
        long duration = 75;
        warningTextView.setText(text);
        warningTextView.setVisibility(View.VISIBLE);
        animateGameOver(warningTextView,0.0f,duration);
        animateGameOver(warningTextView,1.0f,duration);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                warningTextView.setVisibility(View.INVISIBLE);
            }
        }, duration*2);
    }

    private void finishActivity(){
        showAdsInterstitial();
        mGameManager.finish();
        getActivity().finish();
    }

    private void animateGameOver(final TextView view, float alpha, long duration){
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

        public RecyclerGridAdapter(Context context, List<String> data){
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
                return;
            }   else {
                res = R.drawable.empty_cell;
                holder.mImageViewCell.setImageResource(res);
            }
        }
    }

    private class GridViewHolder extends RecyclerView.ViewHolder{

        public ImageView mImageViewCell;
        private int position;

        public GridViewHolder(View itemView) {
            super(itemView);

            mImageViewCell = (ImageView) itemView.findViewById(R.id.cell_image_view);

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


//                        if (stepResult == UNITE_FIGURE
//                                || stepResult == COMBO
//                                || stepResult == DESK_EMPTY
//                                || stepResult == LEVEL_COMPLETE){
//                            showUnitedFigure(mImageViewCell, gamePlay);
//
//                            if (stepResult == COMBO){
//                                showBonusWarning("COMBO!");
//                            }
//
//                            if (stepResult == DESK_EMPTY){
//                                showBonusWarning("+10 BONUS!");
//                            }
//
//                            if (stepResult == LEVEL_COMPLETE){
//                                goingToNextLevel();
//                                return;
//                            }
//                        } else {
//                            replaceFigure(position);
//                        }
                    }   else {
                            boolean isFilled = gamePlay.setAvailableCells(position);
                            if (isFilled){
                                int currentFigureColor = mGameManager.getDesk().getFigure(position).color;

                                mSoundPoolWrapper.playSound(SoundPoolWrapper.SELECT_FIGURE);
                                fillCells(isFilled, currentFigureColor);

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
                        } else {
                        }
                    }
                }
            });
        }

        private void showUnitedFigure(ImageView imageViewCell, GamePlay gamePlay){
            userActionAvailable = false;
            final Handler handler = new Handler();
            final long delay = 150;

            mSoundPoolWrapper.playSound(SoundPoolWrapper.UNITE_FIGURE);

            setImageViewRes(gamePlay.getRecentPosition(), R.drawable.empty_cell);
            imageViewCell.setImageResource(gamePlay.getLastUnitedFigureRes());

            fillCells(false, Color.TRANSPARENT);

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showRecentRandomFiguresWithDelay();
                }
            }, delay);

        }

        public void setPosition(int position) {
            this.position = position;
        }

        private Handler handler;

        private void turnFigure(ImageView imageView){
            final long delay = 150;
            userActionAvailable = false;

            mSoundPoolWrapper.playSound(SoundPoolWrapper.TURN_FIGURE);

            handler = new Handler();
            imageView.animate().rotation(90).setDuration(delay).start();

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    updateFillCells();
                    userActionAvailable = true;

                    if (!mGameManager.getGamePlay().isGameContinue()){
                        updateRecyclerGridDesk();
                    }
                }
            }, delay);
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

        private void showRecentRandomFiguresWithDelay(){
            final List<Figure> recentRandFigures = mGameManager.getGamePlay().getRecentRandomFigures();
            final long delay = 100;

            Handler mHandler = new Handler(){
                int indx = recentRandFigures.size() - 1;

                public void handleMessage(Message msg){
                    super.handleMessage(msg);

                    if(indx > -1) {
                        mSoundPoolWrapper.playSound(SoundPoolWrapper.APPEAR_FIGURE);
                        Figure figure = recentRandFigures.get(indx);
                        int position = mGameManager.getDesk().cellToPosition(figure.mCell);
                        mAdapter.notifyItemChanged(position);
                    }
                        --indx;
                        this.sendEmptyMessageDelayed(0, delay);
                }
            };
            mHandler.sendEmptyMessage(0);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    updateRecyclerGridDesk();
                }
            }, delay*(recentRandFigures.size()));
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