package com.holygunner.game_two;

import com.holygunner.game_two.database.Saver;
import com.holygunner.game_two.figures.Figure;
import com.holygunner.game_two.game_mechanics.*;
import com.holygunner.game_two.values.DeskValues;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
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

public class GameDeskFragment extends Fragment {

    private RecyclerView mRecyclerGridDesk;
    private RecyclerGridAdapter mAdapter;

    private GameManager mGameManager;

    private Button turnFigureButton;
    private TextView gamerCountView;

    private RelativeLayout parentLayout;
    private RelativeLayout gameOverLayout;
    private TextView gameOverTextView;

    public GameDeskFragment(){}

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

//        setIsTurnButtonClickable(Saver.readIsTurnButtonClickable(getContext()));

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

        if (!mGameManager.getGamePlay().isGameContinue()){
            backToStartActivity();
        }
    }

    private void updateGameProcess(){
        List<String> data = new DeskToCellsListConverter(mGameManager.getDesk()).getCellList();
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

//        final Handler handler = new Handler();
//
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                gameOverTextView.setVisibility(View.VISIBLE);
//                animateGameOver(gameOverTextView);
//            }
//        }, 150);

        gameOverTextView.setVisibility(View.VISIBLE);
        animateGameOver(gameOverTextView);



        gameOverLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    backToStartActivity();
                }
        });
    }

    private void backToStartActivity(){
        final Intent intent = new Intent(getContext(), StartGameActivity.class);
        startActivity(intent);
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
            return;
        }   else {
            res = R.drawable.empty_cell_test;
            holder.mImageViewCell.setImageResource(res);
        }
    }

    private class GridViewHolder extends RecyclerView.ViewHolder{
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

//                    if (!gamePlay.isGameContinue()){
//                        gameOver();
//                        return false;
//                    }
                    gamePlay.setRecyclerGridDesk(mRecyclerGridDesk);

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
                        if (gamePlay.fillCells(position)){
                            if (isTurnButtonClickable) {
                                setIsTurnButtonClickable(true);
                            }

                            turnFigureButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    if (isTurnButtonClickable) {
                                        if (mGameManager.getGamePlay().turnFigureIfExists(position)) {
                                            turnFigure(mImageViewCell);
                                        }
                                    }
                                }
                            });
                        }   else {
                            setIsTurnButtonClickable(false);
                        }
                    }

                }
            });


//            mImageViewCell.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    final GamePlay gamePlay = mGameManager.getGamePlay();
//
//                    if (!gamePlay.isGameContinue()){
//                        return;
//                    }
//                    gamePlay.setRecyclerGridDesk(mRecyclerGridDesk);
//
//                    int stepResult;
//
//                    if ((stepResult = gamePlay.tryToStep(position)) != -1){
//                        setIsTurnButtonClickable(false);
//                        isTurnButtonClickable = true;
//
//                        if (stepResult == 1){
//                            showUnitedFigure(mImageViewCell, gamePlay);
//
//                        }   else {
//                            updateGameProcess();
//                        }
//                    }   else {
//                        if (gamePlay.fillCells(position)){
//                            if (isTurnButtonClickable) {
//                                setIsTurnButtonClickable(true);
//                            }
//
//                            turnFigureButton.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//
//                                    if (isTurnButtonClickable) {
//                                        if (gamePlay.turnFigureIfExists(position)) {
//                                            turnFigure(mImageViewCell);
//                                        }
//                                    }
//                                }
//                            });
//                        }   else {
//                            setIsTurnButtonClickable(false);
//                        }
//                        }
//
//                }
//            });
        }

        public void setPosition(int position) {
            this.position = position;
        }
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

    private void turnFigure(ImageView imageView){

        final Handler handler = new Handler();

        imageView.animate().rotation(90).setDuration(150).start();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setIsTurnButtonClickable(false);
                isTurnButtonClickable = false;
                updateGameProcess();
            }
        }, 150);
    }

    private void showUnitedFigure(ImageView imageViewCell, GamePlay gamePlay){

        final Handler handler = new Handler();

        GridViewHolder holder = (GridViewHolder) mRecyclerGridDesk.findViewHolderForAdapterPosition(gamePlay.currentFigurePosition);
        ImageView imageViewCellPrev = (ImageView) holder.mImageViewCell.findViewById(R.id.demo_cell_image_view);

        imageViewCellPrev.setImageResource(R.drawable.empty_cell_test);
        imageViewCell.setImageResource(gamePlay.getLastUnitedFigureRes());

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateGameProcess();
            }
        }, 150);
    }
}