package com.leadmeout.leadmeout;

import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.TextMenuItem;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.util.HorizontalAlign;

import com.example.leadmeout.R;


public class MainMenuScene extends MenuScene implements IOnMenuItemClickListener {
	BaseActivity activity;
	final int MENU_START = 0;
	
	public MainMenuScene(){
		
		super(BaseActivity.getSharedInstance().mCamera);
		activity = BaseActivity.getSharedInstance();

		setBackground(new Background(0.2f, 0.6f, 1.0f));
		final Text gameName = new Text(50, 50 , activity.mMenuFont, activity.getString(R.string.game_name) ,new TextOptions(HorizontalAlign.CENTER),  activity.getVertexBufferObjectManager());
		gameName.setColor(0, 0, 0);
		gameName.setPosition (mCamera.getWidth() / 2 - gameName.getWidth() / 2, mCamera.getHeight() / 2 - gameName.getHeight() / 2 - 50);
		attachChild(gameName);
		
		IMenuItem startButton = new TextMenuItem(MENU_START, activity.mFont,
				activity.getString(R.string.menu_start),
				activity.getVertexBufferObjectManager());
		startButton.setPosition(mCamera.getWidth() / 2 - startButton.getWidth()
				/ 2, mCamera.getHeight() / 2 - startButton.getHeight() / 2 + 100);
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

