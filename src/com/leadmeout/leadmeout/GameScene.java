package com.leadmeout.leadmeout;

import org.andengine.engine.camera.Camera;

import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.ButtonSprite.OnClickListener;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;

import android.util.Log;
import android.widget.Toast;



public class GameScene extends Scene implements IOnSceneTouchListener, OnClickListener {
	BaseActivity activity;
	Camera mCamera;
	
	public GameScene() {
		activity=BaseActivity.getSharedInstance();
		setBackground(new Background(1.0f, 0.5f, 0.0f));
		mCamera=BaseActivity.getSharedInstance().mCamera;
		final Sprite leftButton = new ButtonSprite(400, 400, activity.mLeftArrow, activity.mLeftArrow, activity.mLeftArrow, activity.getVertexBufferObjectManager());
		registerTouchArea(leftButton);
		attachChild(leftButton);
		setOnSceneTouchListener(this);
		setTouchAreaBindingOnActionDownEnabled(true);
	}	

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		// TODO Auto-generated method stub
		
		return false;
	}

	@Override
	public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX,
			float pTouchAreaLocalY) {
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(BaseActivity.getSharedInstance(), "Clicked", Toast.LENGTH_LONG).show();
			}
		});
		
	}


}
