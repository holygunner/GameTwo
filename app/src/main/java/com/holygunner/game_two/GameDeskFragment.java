package com.holygunner.game_two;

import com.holygunner.game_two.database.Saver;
import com.holygunner.game_two.figures.Figure;
import com.holygunner.game_two.figures.FigureFactory;
import com.holygunner.game_two.game_mechanics.*;
import com.holygunner.game_two.values.ColorsValues;
import com.holygunner.game_two.values.LevelsValues;

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
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.List;

import static com.holygunner.game_two.game_mechanics.StepResult.*;

public class GameDeskFragment extends Fragment {

    private RecyclerView mRecyclerGridDesk;
    private RecyclerGridAdapter mAdapter;

    private GameManager mGameManager;

    private Button turnFigureButton;
    private TextView gamerCountView;
    private TextView levelNameTextView;

    private RelativeLayout parentLayout;
    private RelativeLayout gameOverLayout;
    private TextView warningTextView;

    private boolean userActionAvailable;
    private boolean isTurnButtonClickable;

    public GameDeskFragment(){
    }

    public static GameDeskFragment newInstance(){
        return new GameDeskFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.activity_game, container, false);
        parentLayout = (RelativeLayout) view.findViewById(R.id.parentLayout);
        initGameManagerIfNotExists();
        mGameManager.startOrResumeGame(getActivity().getIntent().getIntExtra(
                StartGameActivity.OPEN_LEVEL_NUMB_KEY, 0));

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
        initGameManagerIfNotExists();
        mGameManager.startOrResumeGame(getActivity().getIntent().getIntExtra(
                StartGameActivity.OPEN_LEVEL_NUMB_KEY, 0));
        setIsTurnButtonClickable(mGameManager.getSaver().readIsTurnButtonClickable());
        updateRecyclerGridDesk();
    }

    @Override
    public void onPause(){
        super.onPause();
        mGameManager.save();
        finishActivity();
    }

    private void initGameManagerIfNotExists(){
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
        animateGameOver(warningTextView);
    }

    private void finishActivity(){
        mGameManager.finish();
        getActivity().finish();
    }

    private void animateGameOver(final TextView view){
        view.animate()
                .alpha(0.0f)
                .setDuration(150)
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

            mImageViewCell.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (!userActionAvailable){
                        return false;
                    }
                    final GamePlay gamePlay = mGameManager.getGamePlay();

                    switch (event.getAction()){
                        case MotionEvent.ACTION_DOWN:
                            actionDown(gamePlay);
                            break;
                        case MotionEvent.ACTION_MOVE:
                            break;
                        case MotionEvent.ACTION_UP:
                            break;
                        case MotionEvent.ACTION_CANCEL:
                            break;
                    }
                    return true;
                }

                private void actionDown(GamePlay gamePlay){
                    StepResult stepResult;

                    if ((stepResult = gamePlay.tryToStep(position)) != STEP_UNAVAILABLE){
                        setIsTurnButtonClickable(true);
                        setIsTurnButtonVisible(false);

                        if (stepResult == UNITE_FIGURE
                                || stepResult == DESK_EMPTY
                                || stepResult == LEVEL_COMPLETE){
                            showUnitedFigure(mImageViewCell, gamePlay);

                            if (stepResult == LEVEL_COMPLETE){
                                goingToNextLevel();
                                return;
                            }
                        } else {
                            replaceCell(position);
                        }
                    }   else {
                            boolean isFilled = gamePlay.setAvailableCells(position);
                            if (isFilled){
                                int currentFigureColor = mGameManager.getDesk().getFigure(position).color;
                                fillCells(isFilled, currentFigureColor);

                                if (isTurnButtonClickable) {
                                    setIsTurnButtonClickable(true);
                                }

                                turnFigureButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (isTurnButtonClickable) {
                                            if (mGameManager.getGamePlay().turnFigureIfExists(position)) {
                                                turnFigure(mImageViewCell);
                                            } else {
                                            }
                                        }
                                    }
                                });
                            }
                        }

                }
            });
        }

        private void showUnitedFigure(ImageView imageViewCell, GamePlay gamePlay){
            userActionAvailable = false;
            final Handler handler = new Handler();
            final long delay = 150;

            setImageViewRes(gamePlay.recentPosition, R.drawable.empty_cell);
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
            handler = new Handler();
            imageView.animate().rotation(90).setDuration(delay).start();

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    setIsTurnButtonClickable(false);
                    isTurnButtonClickable = false;

                    updateFillCells();
                    userActionAvailable = true;

                    if (!mGameManager.getGamePlay().isGameContinue()){
                        updateRecyclerGridDesk();
                    }
                }
            }, delay);
        }

        private void replaceCell(int currentPosition){
            if (mRecyclerGridDesk == null){
                return;
            }

            List<Figure> recentRandFigures = mGameManager.getGamePlay().getRecentRandomFigures();

            if (recentRandFigures.isEmpty()){
                return;
            }

            userActionAvailable = false;
            fillCells(false, Color.TRANSPARENT);
            setImageViewRes(mGameManager.getGamePlay().recentPosition, R.drawable.empty_cell);
            setImageViewRes(currentPosition, FigureFactory.getFigureRes(mGameManager.getDesk().getFigure(currentPosition)));
            showRecentRandomFiguresWithDelay();
        }

        private void setImageViewRes(int position, int res){
            GridViewHolder holder = (GridViewHolder) mRecyclerGridDesk.findViewHolderForAdapterPosition(position);
            ImageView imageView = (ImageView) holder.mImageViewCell.findViewById(R.id.cell_image_view);
            imageView.setImageResource(res);
        }

        private void updateFillCells(){
            Figure recentFigure =  mGameManager.getDesk().getFigure(mGameManager.getGamePlay().recentPosition);

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
            setBackgroundColorOnPosition(mGameManager.getGamePlay().recentPosition, currentFigureColor);
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