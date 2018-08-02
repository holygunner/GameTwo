package com.holygunner.game_two;

import com.holygunner.game_two.database.Saver;
import com.holygunner.game_two.figures.Figure;
import com.holygunner.game_two.figures.Position;
import com.holygunner.game_two.game_mechanics.*;
import com.holygunner.game_two.values.ColorValues;
import com.holygunner.game_two.values.DeskValues;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Fade;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import static com.holygunner.game_two.game_mechanics.GameManager.cellToPosition;
import static com.holygunner.game_two.game_mechanics.GameManager.positionToCell;

public class GameDeskFragment extends Fragment {

    private RecyclerView mRecyclerGridDesk;
    private RecyclerGridAdapter mAdapter;

    private GameManager mGameManager;

    private Button turnFigureButton;
    private TextView gamerCountView;

    private RelativeLayout parentLayout;
    private RelativeLayout gameOverLayout;
    private TextView gameOverTextView;

    public GameDeskFragment(){
    }

    public static GameDeskFragment newInstance(){
        return new GameDeskFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.activity_game_upd, container, false);
        parentLayout = (RelativeLayout) view.findViewById(R.id.parentLayout);
        gameOverTextView = (TextView) view.findViewById(R.id.gameOverTextView);
        gameOverLayout = (RelativeLayout) view.findViewById(R.id.gameOverLayout);

        mRecyclerGridDesk = (RecyclerView) view.findViewById(R.id.recycler_grid_game_desk);
        mRecyclerGridDesk.setLayoutManager(new GridLayoutManager(getActivity(), DeskValues.DESK_WIDTH));

        turnFigureButton = (Button) view.findViewById(R.id.turnFigureButton);
        gamerCountView = (TextView) view.findViewById(R.id.gamerCountTextView);
        gamerCountView.setText(readGamerCount());

        boolean isOpenSave = getActivity().getIntent().getBooleanExtra(StartGameActivity.IS_OPEN_SAVE_KEY, false);
        Saver.writeIsSaveExists(getActivity(), isOpenSave);

        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        mGameManager = new GameManager(getActivity());
        mGameManager.startOrResumeGame(Saver.isSaveExists(getContext()));
        setIsTurnButtonClickable(Saver.readIsTurnButtonClickable(getContext()));
        updateGameProcess();
    }

    @Override
    public void onPause(){
        super.onPause();
        mGameManager.save();

        if (!mGameManager.getGamePlay().isGameContinue() || !mGameManager.getGamePlay().isGameStarted()){
            finishActivity();
        }
    }

    private void updateGameProcess(){
        List<String> data = DeskToCellsListConverter.getInstanse().getCellList(mGameManager.getDesk());
        updateGamerCount();

        mAdapter = new RecyclerGridAdapter(getActivity(), data);
        mRecyclerGridDesk.setAdapter(mAdapter);

        if (!mGameManager.getGamePlay().isGameContinue()){
            gameOver();
        }
    }

    private void updateGamerCount(){
        int gamerCount = mGameManager.getGamePlay().getGamerCount();
        gamerCountView.setText(gamerCount + "/" + "100"); // 100 - демо, с раундами будет показывать счет которорый нужно набрать, чтобы перейти к след раунду
    }

    private String readGamerCount(){
        int gamerCount = Saver.readGamerCount(getContext());
        return gamerCount + "/" + "100";
    }

    private void gameOver(){
        parentLayout.setAlpha(0.4f);

        for (int i = 0; i < parentLayout.getChildCount(); i++) {
            View child = parentLayout.getChildAt(i);
            child.setEnabled(false);
        }

        gameOverTextView.setVisibility(View.VISIBLE);
        animateGameOver(gameOverTextView);
        gameOverLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    finishActivity();
                }
        });
    }

    private void finishActivity(){
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
    }

    private void setImage(int position, GridViewHolder holder){
        Cell cell = mGameManager.positionToCell(position);

        Figure figure = mGameManager.getDesk().getFigure(cell);
        int res;

        if (figure != null){
            res = Figure.getFigureRes(figure);
            holder.mImageViewCell.setImageResource(res);
//            animationTest(holder);
//            res = Figure.getFigureResMeth2(figure);
//            holder.mImageViewCell.setImageResource(res);
//            routeImageView(holder, figure, res);
            return;
        }   else {
            res = R.drawable.empty_cell;
            holder.mImageViewCell.setImageResource(res);
        }
    }

    private void animationTest(GridViewHolder holder){
//        final ViewGroup transitionsContainer = (ViewGroup) getActivity().findViewById(R.id.cell_container);

        ImageView imageView = holder.mImageViewCell;

        ViewGroup viewGroup = (ViewGroup) holder.itemView;

        boolean visible = true;
        TransitionSet set = new TransitionSet()
                .addTransition(new Fade())
                .setInterpolator(visible ? new LinearOutSlowInInterpolator() :
                        new FastOutLinearInInterpolator());

//        TransitionManager.beginDelayedTransition(parentLayout, set);
        TransitionManager.beginDelayedTransition(viewGroup, set);
        imageView.setVisibility(visible ? imageView.VISIBLE : imageView.INVISIBLE);
    }

    private void routeImageView(GridViewHolder holder, Figure figure, int res){
        Position position = figure.position;
        float rotation = 0f;

        switch (position){
            case POSITION_ONE:
                break;
            case POSITION_TWO:
                rotation = 180f;
                break;
            case POSITION_THREE:
                rotation = 90f;
                break;
            case POSITION_FOUR:
                rotation = 270f;
                break;
        }
//        holder.mImageViewCell.setRotation(rotation);


        ImageView imageView = holder.mImageViewCell;

        Bitmap myImg = BitmapFactory.decodeResource(getResources(), res);

        Matrix matrix = new Matrix();
        matrix.postRotate(rotation);

        Bitmap rotated = Bitmap.createBitmap(myImg, 0, 0, myImg.getWidth(), myImg.getHeight(),
                matrix, true);

        imageView.setImageBitmap(rotated);
    }

    public class GridViewHolder extends RecyclerView.ViewHolder{
        public ImageView mImageViewCell;
        private int position;

        public GridViewHolder(View itemView) {
            super(itemView);

            mImageViewCell = (ImageView) itemView.findViewById(R.id.demo_cell_image_view);

            setIsTurnButtonVisible(false);

            mImageViewCell.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
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
                    int stepResult;

                    if ((stepResult = gamePlay.tryToStep(position)) != -1){
                        setIsTurnButtonClickable(true);
                        setIsTurnButtonVisible(false);

                        if (stepResult == 1){
                            showUnitedFigure(mImageViewCell, gamePlay);

                        }   else {
                            updateGameProcess();
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
//                                            showAddedFigureWithDelay();

//                                            GridViewHolder holder = (GridViewHolder) mRecyclerGridDesk.findViewHolderForAdapterPosition(mGameManager.getGamePlay().currentFigurePosition);
//                                            ImageView imageViewCellPrev = (ImageView) holder.mImageViewCell.findViewById(R.id.demo_cell_image_view);
//                                            imageViewCellPrev.performClick();
                                        } else {
                                        }
                                    }
                                }
                            });
                        }   else {
//                            setIsTurnButtonClickable(false);
                        }
                    }

                }
            });
        }

        public void setPosition(int position) {
            this.position = position;
        }
    }

//    private void showAddedFigureWithDelay(){
//        if (mRecyclerGridDesk == null){
//            return;
//        }
//
//        List<Figure> recentRandomFigures = mGameManager.getGamePlay().getRecentRandomFigures();
//
//        if (recentRandomFigures.isEmpty()){
//            return;
//        }
//
//        for(Figure figure: recentRandomFigures) {
//            int position = cellToPosition(figure.mCell);
//            final int res = Figure.getFigureRes(figure);
//
//            GameDeskFragment.GridViewHolder holder = (GameDeskFragment.GridViewHolder) mRecyclerGridDesk.findViewHolderForAdapterPosition(position);
//            if (holder == null){
//                return;
//            }
//            final ImageView imageViewCellPrev = (ImageView) holder.mImageViewCell.findViewById(R.id.demo_cell_image_view);
//            final Handler handler = new Handler();
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    imageViewCellPrev.setImageResource(res);
////                    imageViewCellPrev.setImageResource(R.drawable.empty_cell);
//                }
//            }, 500);
//        }
//    }


    private void fillCells(boolean isFill, int currentFigureColor){
        int color = getCellsColor(isFill);
        Desk desk = mGameManager.getDesk();
        GamePlay.AvailableSteps availableSteps = mGameManager.getGamePlay().getAvailableSteps();

        for (int y = 0; y<desk.getArr().length; y++){
            for (int x = 0; x<desk.getArr()[y].length; x++){
                Cell cell = new Cell(x, y);
                int position = cellToPosition(cell);

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

        if (color == ColorValues.FillColors.CURRENT_FIGURE_FILL){
        }   else {
            currentFigureColor = Color.TRANSPARENT;
        }

        setBackgroundColorOnPosition(mGameManager.getGamePlay().currentFigurePosition, currentFigureColor);
    }

    private int getCellsColor(boolean isFillColor){
        int color;

        if (isFillColor) {
            color = ColorValues.FillColors.CURRENT_FIGURE_FILL;
            mGameManager.getGamePlay().setIsFilled(true);
        }   else {
            color = Color.TRANSPARENT;
            mGameManager.getGamePlay().setIsFilled(false);
        }
        return color;
    }

    private boolean isTurnButtonClickable;

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

    public void updateFillCells(){
        Figure recentFigure =  mGameManager.getDesk().getFigure(mGameManager.getGamePlay().currentFigurePosition);
        int currentFigureColor = recentFigure.color;
        fillCells(true, currentFigureColor);

        Figure addedRandomFigure = mGameManager.getGamePlay().getRecentRandomFigures().get(0);

        GridViewHolder holder = (GridViewHolder) mRecyclerGridDesk.findViewHolderForAdapterPosition(cellToPosition(addedRandomFigure.mCell));
        ImageView imageViewCellPrev = (ImageView) holder.mImageViewCell.findViewById(R.id.demo_cell_image_view);
        imageViewCellPrev.setImageResource(Figure.getFigureRes(addedRandomFigure));
    }

    private void turnFigure(ImageView imageView){
        updateFillCells();

        final Handler handler = new Handler();
        imageView.animate().rotation(90).setDuration(150).start();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setIsTurnButtonClickable(false);
                isTurnButtonClickable = false;

                if (!mGameManager.getGamePlay().isGameContinue()){
                    updateGameProcess();
                }
            }
        }, 150);

//        RotateAnimation rotate = new RotateAnimation(180, 360, Animation.RELATIVE_TO_SELF,
//                0.5f,  Animation.RELATIVE_TO_SELF, 0.5f);
//        rotate.setDuration(500);
//        imageView.startAnimation(rotate);
    }

    private void showUnitedFigure(ImageView imageViewCell, GamePlay gamePlay){

        final Handler handler = new Handler();

        GridViewHolder holder = (GridViewHolder) mRecyclerGridDesk.findViewHolderForAdapterPosition(gamePlay.currentFigurePosition);
        ImageView imageViewCellPrev = (ImageView) holder.mImageViewCell.findViewById(R.id.demo_cell_image_view);

        imageViewCellPrev.setImageResource(R.drawable.empty_cell);
        imageViewCell.setImageResource(gamePlay.getLastUnitedFigureRes());

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateGameProcess();
            }
        }, 150);
    }

    public void setBackgroundColorOnPosition(int position, int color){
        mRecyclerGridDesk.getLayoutManager().findViewByPosition(position).setBackgroundColor(color);
    }
}