package com.chubbygirl.main;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import java.util.ArrayDeque;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

/**
 *进入直播间
 */
public class EnterRoomTextView extends AppCompatTextView {


    public EnterRoomTextView(Context context) {
        super(context);
    }

    public EnterRoomTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public EnterRoomTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    //某某进入直播间
    private AnimatorSet aniSet = new AnimatorSet();
    private AnimatorSet aniSetMoveOut = new AnimatorSet();
    private ArrayDeque<String> someoneInMsgs = new ArrayDeque<>();

    private void initSomeoneInAnimator() {
        final ObjectAnimator moveIn = ObjectAnimator.ofFloat(this, "translationX",
                -1000f, 0f);
        final ObjectAnimator moveOut = ObjectAnimator.ofFloat(this, "translationX",
                1000f);
        final ObjectAnimator fadeInOut = ObjectAnimator.ofFloat(this, "alpha", 1f, 0f);

        aniSetMoveOut.playTogether(moveOut, fadeInOut);
        aniSetMoveOut.setStartDelay(2000);
        aniSet.play(moveIn).before(aniSetMoveOut);
        aniSet.setDuration(1000);
    }

    public void sendSomeoneInMsg(boolean isMe, String msgContent) {
        final Message msg = someoneInMsghandler.obtainMessage();
        msg.obj = msgContent;
        msg.arg1 = isMe?1:0;
        someoneInMsghandler.sendMessage(msg);
    }

    Handler someoneInMsghandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            String s = (String) msg.obj;
            if (msg.arg1 == 0){
                someoneInMsgs.addLast(s);
            }else{
                someoneInMsgs.addFirst(s);
            }

            return false;
        }
    });


    public void pollingSomeoneInMsgs() {
        if (someoneInMsgs.size() != 0 && !aniSet.isRunning()) {
            String s = someoneInMsgs.getFirst();
            setText(s);
            aniSet.start();
            someoneInMsgs.poll();
        }
    }

    protected Disposable mDisposable;

    private void initTimer(long milliseconds) {
        Observable.interval(milliseconds, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable disposable) {
                        mDisposable = disposable;
                    }

                    @Override
                    public void onNext(@NonNull Long number) {
                        //轮询有人进入直播间消息列表
                        pollingSomeoneInMsgs();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        initSomeoneInAnimator();
        initTimer(1000);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mDisposable!=null&&!mDisposable.isDisposed()){
            mDisposable.dispose();
        }
    }
}
