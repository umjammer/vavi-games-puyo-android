/*
 * Copyright (c) 2009 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.games.puyopuyo;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;


/**
 * PuyoPuyoApp
 * 
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 090106 nsano initial version <br>
 */
public class PuyoPuyoApp extends Activity {

    /* */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        MyView view = new MyView(this);
        setContentView(view);
        view.start();
    }

    /** */
    private class MyView extends View implements PuyoPuyo.View {
        /** */
        private PuyoPuyo.Stage stage;
    
        /** */
        private Bitmap offscreenImage;
        /** */
        private Canvas ofscreenGraphics;
        /** */
        private Paint paint = new Paint();
        /** */
        private Bitmap wallImage;
        /** */
        private Bitmap fieldImage;
        /** */
        private Bitmap nextImage;
        /** */
        private Bitmap[] images;
    
        /** */
        private int offScreenWidth;
        /** */
        private int offScreenHeight;
        /** */
        private MediaPlayer[] clips;
    
        /** */
        private int[] fieldLefts, fieldTops;
        /** �Ղ�̕� */
        private int puyoSize;
    
        /** �Q�[�����X�^�[�g���������� */
        private int startFlag;
        /** �ꎞ��~�E�X�^�[�g���� */
        private int stopFlag;
    
        private String state = ""; // "test";
    
        /** ���y�̒�~ */
        public void stopClips() {
            for (int i = 0; i < clips.length; i++) {
                clips[i].stop();
            }
        }
    
        /** */
        MyView(Context context) {
            super(context);

            setFocusable(true);

            Resources r = getContext().getResources();

            // �p�����[�^���擾
            int playersCount = Integer.parseInt(r.getString(R.string.main_activity_n));
            stage = new PuyoPuyo.Stage(playersCount);
            offScreenWidth = Integer.parseInt(r.getString(R.string.main_activity_w));
            offScreenHeight = Integer.parseInt(r.getString(R.string.main_activity_h));
            stage.set = Integer.parseInt(r.getString(R.string.main_activity_s));
            stage.soundFlag = Integer.parseInt(r.getString(R.string.main_activity_v));
            stage.puyoFlag = Integer.parseInt(r.getString(R.string.main_activity_p));
            // ���z��ʂ��`
            offscreenImage = Bitmap.createBitmap(offScreenWidth, offScreenHeight, Bitmap.Config.ARGB_8888);
            ofscreenGraphics = new Canvas(offscreenImage);
            // ���y�t�@�C���ǂݍ���
            clips = new MediaPlayer[6];
            clips[0] = MediaPlayer.create(context, R.raw.puyo_08); // BGM
            clips[1] = MediaPlayer.create(context, R.raw.a728); // �I��
            clips[2] = MediaPlayer.create(context, R.raw.pyoro22); // �ړ�
            clips[3] = MediaPlayer.create(context, R.raw.puu58); // ��]
            clips[4] = MediaPlayer.create(context, R.raw.puu47); // �ςݏグ
            clips[5] = MediaPlayer.create(context, R.raw.open23); // �������
            // �摜�ǂݍ���
            wallImage = BitmapFactory.decodeResource(r, R.drawable.wall);
            fieldImage = BitmapFactory.decodeResource(r, R.drawable.dt13);
            nextImage = BitmapFactory.decodeResource(r, R.drawable.next);
            images = new Bitmap[12];
            images[0] = BitmapFactory.decodeResource(r, R.drawable.back);
            images[1] = BitmapFactory.decodeResource(r, R.drawable.gray);
            images[2] = BitmapFactory.decodeResource(r, R.drawable.red);
            images[3] = BitmapFactory.decodeResource(r, R.drawable.yellow);
            images[4] = BitmapFactory.decodeResource(r, R.drawable.blue);
            images[5] = BitmapFactory.decodeResource(r, R.drawable.green);
            images[6] = BitmapFactory.decodeResource(r, R.drawable.purple);
            // ������
            stage.init();
            puyoSize = 16;
            startFlag = 0;
            stopFlag = 0;
        }
    
        /** */
        @Override
        public boolean onKeyDown(int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_R) {
                // ���Z�b�g
                stopClips();
                stage.init();
                puyoSize = 16;
                startFlag = 0;
                stopFlag = 0;
                for (int i = 0; i < stage.playersCount; i++) {
                    stage.games[i].init();
                }
                repaint();
            } else if (startFlag == 0 && keyCode == KeyEvent.KEYCODE_S) {
                // �X�^�[�g
                startFlag = 1;
                if (stage.soundFlag == 1) {
                    clips[0].setLooping(true);
                    clips[0].start();
                }
                for (int i = 0; i < stage.playersCount; i++) {
                    stage.games[i].start();
                }
            } else if (startFlag == 1 && stage.games[0].waitFlag == 0) {
                // �Q�[�����X�^�[�g���Ă�����

                // �X�g�b�v
                if (keyCode == KeyEvent.KEYCODE_S && stage.playersCount == 1) {
                    if (stopFlag == 0) {
                        stage.games[0].waitFlag = 1;
                        stage.games[0].sleep(PuyoPuyo.FallSpeed, "Stop");
                    } else {
                        stage.games[0].autoFall();
                    }
                    // �Q�[���ĊJ���ꎞ��~
                    if (stopFlag == 1) {
                        stopFlag = 0;
                    } else {
                        stopFlag = 1;
                    }
                    // �`��
                    repaint();
                }
                // �ړ��E��]�E�I�[�g���[�h
                if (stopFlag == 0 && stage.games[0].waitFlag == 0) {
                    if (keyCode == KeyEvent.KEYCODE_X) { // ��]
                        stage.games[0].rotate(1);
                    } else if (keyCode == KeyEvent.KEYCODE_Z) { // ��]
                        stage.games[0].rotate(2);
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) { // ���ړ�
                        stage.games[0].left();
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) { // �E�ړ�
                        stage.games[0].right();
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) { // ���ړ�
                        stage.games[0].down();
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) { // ��ړ�
                        stage.games[0].up();
                    } else if (keyCode == KeyEvent.KEYCODE_SPACE) { // ��C�ɉ��ړ�
                        stage.games[0].bottom();
                    } else if (keyCode == KeyEvent.KEYCODE_A) { // �I�[�g���[�h�؂�ւ�
                        if (stage.games[0].autoFlag == 0) {
                            stage.games[0].autoFlag = 1;
                            stage.games[0].autoMove();
                        } else {
                            stage.games[0].autoFlag = 0;
                        }
                        repaint();
                    } else if (keyCode == KeyEvent.KEYCODE_R) { // repaint
                        repaint();
                    }
                }
            }
            return super.onKeyDown(keyCode, event);
        }
    
        /* */
        void start() {
            // �t�H�[�J�X�����킹��
            this.requestFocus();
            // �I�u�W�F�N�g���`
            fieldLefts = new int[stage.playersCount];
            fieldTops = new int[stage.playersCount];
            for (int i = 0; i < stage.playersCount; i++) {
                stage.games[i] = new PuyoPuyo(stage, i);
                stage.games[i].setView(this);
                // �t�B�[���h�J�n�ʒu
                fieldLefts[i] = (i % 4) * ((stage.columns + 2) * puyoSize + 100);
                fieldTops[i] = (i - i % 4) / 4 * (stage.lows * puyoSize + 44);
            }
        }
    
        /* */
        void stop() {
            for (int i = 0; i < stage.playersCount; i++) {
                stage.gameFlags[i] = 0;
            }
            stopClips();
        }
    
        /** */
        public void repaint() {
            postInvalidate();
        }

        /* */
        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            // �t�B�[���h��\��
            for (int n = 0; n < stage.playersCount; n++) {
                // �w�i�摜
                ofscreenGraphics.drawBitmap(fieldImage, fieldLefts[n], fieldTops[n], null);
                // ��
                for (int i = 2; i < stage.lows; i++) {
                    ofscreenGraphics.drawBitmap(wallImage, fieldLefts[n], i * puyoSize + fieldTops[n], null);
                    ofscreenGraphics.drawBitmap(wallImage, fieldLefts[n] + (stage.columns + 1) * puyoSize, i * puyoSize + fieldTops[n], null);
                }
                for (int j = 0; j < stage.columns + 2; j++) {
                    ofscreenGraphics.drawBitmap(wallImage, fieldLefts[n] + j * puyoSize, stage.lows * puyoSize + fieldTops[n], null);
                }
                // �Ղ�
                for (int i = 2; i < stage.lows; i++) {
                    for (int j = 0; j < stage.columns; j++) {
                        ofscreenGraphics.drawBitmap(images[0], puyoSize + j * puyoSize + fieldLefts[n], i * puyoSize + fieldTops[n], null);
                        ofscreenGraphics.drawBitmap(images[stage.games[n].grid[i][j]], puyoSize + j * puyoSize + fieldLefts[n], i * puyoSize + fieldTops[n], null);
                    }
                }
                // �\���������
                ofscreenGraphics.drawBitmap(images[1], puyoSize + fieldLefts[n], puyoSize + fieldTops[n], null);
                // ����
                paint.setARGB(0xff, 0, 0, 0);
                ofscreenGraphics.drawText("Next", 137 + fieldLefts[n], 41 + fieldTops[n], paint); // NEXT
                ofscreenGraphics.drawText("" + stage.disturbCounts[n], 38 + fieldLefts[n], 30 + fieldTops[n], paint); // �������
                ofscreenGraphics.drawText(stage.games[n].overMessage, 135 + fieldLefts[n], 130 + fieldTops[n], paint); // �Q�[���I�[�o�[
                if (stopFlag == 1) { // Stop
                    ofscreenGraphics.drawText("STOP", 135 + fieldLefts[n], 190 + fieldTops[n], paint);
                }
                if (state != "test") {
                    ofscreenGraphics.drawText("Score: " + stage.games[n].score, 135 + fieldLefts[n], 170 + fieldTops[n], paint); // Score
                    if (stage.games[n].autoFlag == 1) { // Auto
                        ofscreenGraphics.drawText("AUTO", 135 + fieldLefts[n], 210 + fieldTops[n], paint);
                    }
                }
                // �A��
                if (stage.games[n].chainCount >= 1) {
                    ofscreenGraphics.drawText(stage.games[n].message + "�I", 135 + fieldLefts[n], 130 + fieldTops[n], paint);
                    ofscreenGraphics.drawText("(" + stage.games[n].chainCount + "�A��)", 135 + fieldLefts[n], 145 + fieldTops[n], paint);
                }
                // ���Ղ�
                if ((stage.games[n].waitFlag == 0 || stopFlag == 1) && stage.gameFlags[n] == 1) {
                    if (stage.games[n].pos[0][0] > 1) {
                        ofscreenGraphics.drawBitmap(images[stage.games[n].puyo1], (stage.games[n].pos[0][1] + 1) * puyoSize + fieldLefts[n], (stage.games[n].pos[0][0]) * puyoSize + fieldTops[n], null);
                    }
                    if (stage.games[n].pos[1][0] > 1) {
                        ofscreenGraphics.drawBitmap(images[stage.games[n].puyo2], (stage.games[n].pos[1][1] + 1) * puyoSize + fieldLefts[n], (stage.games[n].pos[1][0]) * puyoSize + fieldTops[n], null);
                    }
                }
                // NEXT�Ղ�
                ofscreenGraphics.drawBitmap(nextImage, 138 + fieldLefts[n], 47 + fieldTops[n], null);
                ofscreenGraphics.drawBitmap(images[stage.games[n].npuyo1], 143 + fieldLefts[n], 51 + fieldTops[n], null);
                ofscreenGraphics.drawBitmap(images[stage.games[n].npuyo2], 143 + fieldLefts[n], 67 + fieldTops[n], null);
                ofscreenGraphics.drawBitmap(images[stage.games[n].nnpuyo1], 159 + fieldLefts[n], 59 + fieldTops[n], null);
                ofscreenGraphics.drawBitmap(images[stage.games[n].nnpuyo2], 159 + fieldLefts[n], 75 + fieldTops[n], null);
                // �e�X�g�p
                if (state.equals("test")) {
                    paint.setARGB(0xff, 0, 0, 0);
                    // ����
                    ofscreenGraphics.drawText(stage.games[n].x, 10 + fieldLefts[n], 260 + fieldTops[n], paint); // "Chain=" + G[n].max_chain_num + ", " +
                    // �z��
                    for (int i = 0; i < stage.lows; i++) {
                        for (int j = 0; j < stage.columns; j++) {
                            if (stage.games[n].lastIgnitionLabel2[i][j] > 1) {
                                paint.setARGB(0xff, 255, 0, 0);
                            } else if (stage.games[n].lastIgnitionLabel2[i][j] == 1) {
                                paint.setARGB(0xff, 0, 0, 255);
                            } else {
                                paint.setARGB(0xff, 0, 0, 0);
                            }
                            ofscreenGraphics.drawText("" + stage.games[n].lastChainLabel[i][j], 11 * j + 150 + fieldLefts[n], 11 * i + 100 + fieldTops[n], paint);
                        }
                    }
                }
            }
            // �ꊇ�\��
            canvas.drawBitmap(offscreenImage, 0, 0, null);
        }
    
        /* */
//        @Override
        public void play(int folder, int cn) {
//            play(getDocumentBase(), "sound/chain/" + folder + "/" + cn + ".au");
        }
    
        /* */
//        @Override
        public void playClip(int i) {
            clips[i].start();
        }
    }
}

/* */