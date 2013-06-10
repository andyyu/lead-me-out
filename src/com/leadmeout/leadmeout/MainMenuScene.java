package com.leadmeout.leadmeout;

import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.TextMenuItem;
import org.andengine.entity.sprite.Sprite;

import com.example.leadmeout.R;


public class MainMenuScene extends MenuScene implements IOnMenuItemClickListener {
	BaseActivity activity;
	final int MENU_START = 0;
	final float CAMERA_WIDTH = mCamera.getWidth();
	final float CAMERA_HEIGHT = mCamera.getHeight();
	
	public MainMenuScene(){
		
		super(BaseActivity.getSharedInstance().mCamera);
		
		activity = BaseActivity.getSharedInstance();
		final int centerX = (int) ((CAMERA_WIDTH -
				BaseActivity.getSharedInstance().mBgTexture.getWidth()) / 2); final int centerY = (int) ((CAMERA_HEIGHT -
						BaseActivity.getSharedInstance().mBgTexture.getHeight()) / 2);
        SpriteBackground bg = new SpriteBackground(new Sprite(centerX, centerY, BaseActivity.getSharedInstance().mBgTexture, BaseActivity.getSharedInstance().vbo));
        this.setBackground(bg);

		
		/**
		final IMenuItem playMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_START, BaseActivity.getSharedInstance().playbutton, BaseActivity.getSharedInstance().vbo), 1.2f, 1);
		addMenuItem(playMenuItem);    
		buildAnimations();
		setBackgroundEnabled(false);	    
		playMenuItem.setPosition(CAMERA_WIDTH / 2 , CAMERA_HEIGHT / 2);
		**/
		
		
		IMenuItem startButton = new TextMenuItem(MENU_START, BaseActivity.getSharedInstance().mFont,
				BaseActivity.getSharedInstance().getString(R.string.menu_start),
				BaseActivity.getSharedInstance().getVertexBufferObjectManager());
		startButton.setPosition(CAMERA_WIDTH / 2 - startButton.getWidth()
				/ 2, CAMERA_HEIGHT / 2 - startButton.getHeight() / 2 + 100);
		
		startButton.setColor(0, 0, 0);
		addMenuItem(startButton);
		
		
		setOnMenuItemClickListener(this);
		
	}
	
	
	public boolean onMenuItemClicked(MenuScene arg0, IMenuItem arg1, float arg2, float arg3) {
	    switch (arg1.getID()) {
	        case MENU_START:
	            activity.setCurrentScene(new DrawingScene());
	            return true;
	        default:
	            break;
	    }
	    return false;
	}

}

