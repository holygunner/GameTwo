package com.holygunner.halves_into_whole;

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

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Objects;

import static com.holygunner.halves_into_whole.game_mechanics.StepResult.*;

public class GameDeskFragment extends Fragment {

    private RecyclerView mRecyclerGridDesk;
    private RecyclerGridAdapter mAdapter;
    private Button mTurnFigureButton;
    private TextView mGamerCountView;
    private TextView mLevelNameTextView;
    private ViewGroup mParentLayout;
    private ViewGroup mGameOverLayout;
    private TextView mWarningTextView;

    private boolean mUserActionAvailable;
    private boolean mIsTurnButtonClickable;

    private GameManager mGameManager;
    private SoundPoolWrapper mSoundPoolWrapper;

    private static final long TURN_FIGURE_DELAY = 150;
    private static final long APPEAR_NEW_FIGURE_DELAY = 100;

    public GameDeskFragment(){
    }

    @NonNull
    public static GameDeskFragment newInstance(){
        return new GameDeskFragment();
    }

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mSoundPoolWrapper = SoundPoolWrapper.getInstance(getActivity());

        initGameManager();
        mGameManager.startOrResumeGame(Objects.requireNonNull(getActivity()).getIntent()
                .getIntExtra(StartGameActivity.OPEN_LEVEL_NUMB_KEY, 0));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
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
        finishActivity();
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

        mSoundPoolWrapper.playSound(getContext(), SoundPoolWrapper.LEVEL_LOSE);

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
        String nextLevelStr = LevelsValues.getLevelName(Objects.requireNonNull(getContext()),
                nextLevelNumb);
        mLevelNameTextView.setVisibility(View.INVISIBLE);
        prepareViewsForFinish(nextLevelStr);

        mSoundPoolWrapper.playSound(getContext(), SoundPoolWrapper.LEVEL_COMPLETE);

        mGameOverLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), GameFragmentActivity.class);
                intent.putExtra(StartGameActivity.OPEN_LEVEL_NUMB_KEY,
                        mGameManager.getGamePlay().getLevelNumb());
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
        mSoundPoolWrapper.playSound(getContext(), SoundPoolWrapper.COMBO);
        animateWarningTextView(mWarningTextView,alphaEnd,duration);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mWarningTextView.setVisibility(View.INVISIBLE);
            }
        }, duration);
    }

    private void finishActivity(){
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
        ImageView mImageViewCell;
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

                private void actionDown(@NonNull GamePlay gamePlay){
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
                                showBonusWarning(getString(R.string.combo));
                                break;
                            case DESK_EMPTY:
                                showUnitedFigure(mImageViewCell, gamePlay);
                                showBonusWarning(getString(R.string.bonus));
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
                            int currentFigureColor = mGameManager.getDesk()
                                    .getFigure(position).color;

                            mSoundPoolWrapper.playSound(getContext(), SoundPoolWrapper.SELECT_FIGURE);
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

        public void setPosition(int position) {
            this.position = position;
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

        private void showUnitedFigure(@NonNull ImageView imageViewCell,
                                      @NonNull GamePlay gamePlay){
            mUserActionAvailable = false;
            final Handler HANDLER = new Handler();

            mSoundPoolWrapper.playSound(getContext(), SoundPoolWrapper.UNITE_FIGURE);
            setImageViewRes(gamePlay.getRecentPosition(), R.drawable.empty_cell);
            imageViewCell.setImageResource(gamePlay.getLastUnitedFigureRes());

            fillCells(false, Color.TRANSPARENT);

            HANDLER.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showRecentRandomFiguresWithDelay();
                }
                }, TURN_FIGURE_DELAY);
        }

        private void turnFigure(@NonNull ImageView imageView){
            mUserActionAvailable = false;

            mSoundPoolWrapper.playSound(getContext(), SoundPoolWrapper.TURN_FIGURE);

            imageView.animate().rotation(90).setDuration(TURN_FIGURE_DELAY).start();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    updateFillCells();
                    mUserActionAvailable = true;

                    if (!mGameManager.getGamePlay().isGameContinue()){
                        updateRecyclerGridDesk();
                    }
                }
            }, TURN_FIGURE_DELAY);
        }

        private void replaceFigure(int currentPosition){
            if (mRecyclerGridDesk == null){
                return;
            }

            List<Figure> recentRandFigures = mGameManager.getGamePlay().getRecentRandomFigures();

            if (recentRandFigures.isEmpty()){
                return;
            }

            mSoundPoolWrapper.playSound(getContext(), SoundPoolWrapper.REPLACE_FIGURE);
            mUserActionAvailable = false;
            fillCells(false, Color.TRANSPARENT);
            setImageViewRes(mGameManager.getGamePlay().getRecentPosition(), R.drawable.empty_cell);
            setImageViewRes(currentPosition, FigureFactory.getFigureRes(mGameManager.getDesk()
                    .getFigure(currentPosition)));
            showRecentRandomFiguresWithDelay();
        }

        private void setImageViewRes(int position, int res){
            GridViewHolder holder
                    = (GridViewHolder) mRecyclerGridDesk.findViewHolderForAdapterPosition(position);
            assert holder != null;
            ImageView imageView = holder.mImageViewCell.findViewById(R.id.cell_image_view);
            imageView.setImageResource(res);
        }

        private void updateFillCells(){
            Figure recentFigure =  mGameManager.getDesk().getFigure(mGameManager.getGamePlay()
                    .getRecentPosition());

        if (!mGameManager.getGamePlay().getRecentRandomFigures().isEmpty()){
            Figure addedRandomFigure = mGameManager.getGamePlay().getRecentRandomFigures().get(0);
            setImageViewRes(mGameManager.getDesk().cellToPosition(addedRandomFigure.cell),
                    FigureFactory.getFigureRes(addedRandomFigure));
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
            setBackgroundColorOnPosition(mGameManager.getGamePlay().getRecentPosition(),
                    currFigureColor);
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
            Objects.requireNonNull(Objects.requireNonNull(mRecyclerGridDesk.getLayoutManager())
                    .findViewByPosition(position))
                    .setBackgroundColor(color);
        }

        private int getCellsColor(boolean isFillColor){
            int color;

            if (isFillColor) {
                color = ContextCompat.getColor(Objects.requireNonNull(getContext()),
                        R.color.currentFigureFill);
                mGameManager.getGamePlay().setIsFilled(true);
            }   else {
                color = Color.TRANSPARENT;
                mGameManager.getGamePlay().setIsFilled(false);
            }
            return color;
        }

        private void showRecentRandomFiguresWithDelay(){
            final List<Figure> RECENT_RAND_FIGURES
                    = mGameManager.getGamePlay().getRecentRandomFigures();

            AppearFigureHandler handler = new AppearFigureHandler(getContext(), mSoundPoolWrapper,
                    RECENT_RAND_FIGURES, mGameManager, mAdapter);
            handler.sendEmptyMessage(0);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    updateRecyclerGridDesk();
                }
            }, APPEAR_NEW_FIGURE_DELAY*(RECENT_RAND_FIGURES.size()));
        }
    }

    private static class AppearFigureHandler extends Handler{
        private WeakReference<Context> mContextWeakRef;
        private WeakReference<SoundPoolWrapper> mSoundPoolWrapperWeakRef;
        private WeakReference<List<Figure>> mRecentFiguresWeakRef;
        private WeakReference<GameManager> mGameManagerWeakRef;
        private WeakReference<RecyclerGridAdapter> mAdapterWeakRef;
        private int index;

        AppearFigureHandler(Context context, SoundPoolWrapper soundPoolWrapper, List<Figure> recentFigures,
                            GameManager gameManager, RecyclerGridAdapter mAdapter){
            mContextWeakRef = new WeakReference<>(context);
            mSoundPoolWrapperWeakRef = new WeakReference<>(soundPoolWrapper);
            mRecentFiguresWeakRef = new WeakReference<>(recentFigures);
            mGameManagerWeakRef = new WeakReference<>(gameManager);
            mAdapterWeakRef = new WeakReference<>(mAdapter);
            index = recentFigures.size() - 1;
        }

        @Override
        public void handleMessage(Message msg) {
            if (mContextWeakRef != null && mSoundPoolWrapperWeakRef != null
                    && mRecentFiguresWeakRef != null && mGameManagerWeakRef != null
                    && mAdapterWeakRef != null) {
                super.handleMessage(msg);

                List<Figure> recentFigures = mRecentFiguresWeakRef.get();
                SoundPoolWrapper soundPoolWrapper = mSoundPoolWrapperWeakRef.get();

                if (index > -1) {
                    soundPoolWrapper.playSound(mContextWeakRef.get(),
                            SoundPoolWrapper.APPEAR_FIGURE);
                    Figure figure = recentFigures.get(index);
                    int position = mGameManagerWeakRef.get().getDesk().cellToPosition(figure.cell);
                    mAdapterWeakRef.get().notifyItemChanged(position);
                }
                --index;
                this.sendEmptyMessageDelayed(0, APPEAR_NEW_FIGURE_DELAY);
            }
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