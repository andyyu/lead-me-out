package com.leadmeout.leadmeout;


import java.io.IOException;
import java.io.InputStream;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.input.touch.TouchEvent;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;



public class GameScene extends Scene implements IOnSceneTouchListener {
	BaseActivity activity;
	PhysicsWorld physicsWorld;
	Camera mCamera;
	Player player;
	Rectangle floor;
	Context ctx;
	
	public GameScene(Context context) {
		ctx=context;
		activity=BaseActivity.getSharedInstance();
		setBackground(new Background(1.0f, 0.5f, 0.0f));
		mCamera=BaseActivity.getSharedInstance().mCamera;
		readLevel();
		createPhysics();
		createBackground();
		createPlayer();
		createHUD();		
		setOnSceneTouchListener(this);
		setTouchAreaBindingOnActionDownEnabled(true);
	}	
	
	public void createPhysics(){
		physicsWorld = new FixedStepPhysicsWorld(60, new Vector2(0, 17), false); 
		physicsWorld.setContactListener(contactListener());
		registerUpdateHandler(physicsWorld);
	}
	
	private ContactListener contactListener()
    {
            ContactListener contactListener = new ContactListener()
            {
                    @Override
                    public void beginContact(Contact contact)
                    {
                           
                    }

                    @Override
                    public void endContact(Contact contact)
                    {
                           
                    }

                    @Override
                    public void preSolve(Contact contact, Manifold oldManifold)
                    {
                           
                    }

                    @Override
                    public void postSolve(Contact contact, ContactImpulse impulse)
                    {
                           
                    }
            };
            return contactListener;
    }
	
	public void createBackground(){
		final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0, 0.2f, 0);
		floor= new Rectangle(0,480, 800, 1,BaseActivity.getSharedInstance().vbo);
		floor.setVisible(false);
		//PhysicsFactory.createBoxBody(this.physicsWorld, floor, BodyType.StaticBody, wallFixtureDef);
		attachChild(floor);
		floorCollision();
	}
	
	public void createPlayer(){
		player=new Player(0, 350, BaseActivity.getSharedInstance().mPlayer, BaseActivity.getSharedInstance().getVertexBufferObjectManager(), physicsWorld);
		attachChild(player);
	}
	
	public void createHUD(){
		final Sprite leftButton = new ButtonSprite(500, 350, activity.mLeftArrow, activity.mLeftArrow, activity.mLeftArrow, activity.getVertexBufferObjectManager()){
			 @Override
		       public boolean onAreaTouched(TouchEvent pTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
		           if(pTouchEvent.isActionDown()) {
		        	   player.moveLeft();
		        	   Log.v("mine","left move");
		           }
		           if(pTouchEvent.isActionUp()){
		        	   player.stop();
		           }
		           return super.onAreaTouched(pTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
		       }
		};
		final Sprite rightButton = new ButtonSprite(600, 350, activity.mRightArrow, activity.mRightArrow, activity.mRightArrow, activity.getVertexBufferObjectManager()){
			 @Override
		       public boolean onAreaTouched(TouchEvent pTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
		           if(pTouchEvent.isActionDown()) {
		        	   player.moveRight();
		        	   Log.v("mine","right move");
		           }
		           if(pTouchEvent.isActionUp()){
		        	   player.stop();
		           }
		           return super.onAreaTouched(pTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
		       }
		};
		registerTouchArea(leftButton);
		attachChild(leftButton);	
		registerTouchArea(rightButton);
		attachChild(rightButton);
	}
	
	public void floorCollision(){

		registerUpdateHandler(new IUpdateHandler() {
			@Override
			public void reset() { }
	
			@Override
			public void onUpdate(final float pSecondsElapsed) {
				if(player.collidesWith(floor)) {
					setBackground(new Background(1.0f, 0.0f, 0.2f));
				} 	
			}
		});
		
	}
	
	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		// TODO Auto-generated method stub
		if(pSceneTouchEvent.isActionDown()) {
     	   player.jump();
     	   Log.v("mine","right move");
     	   
        }
        if(pSceneTouchEvent.isActionUp()){
     	   player.stop();
        }
		return false;
	}
	
	public void readLevel(){
		try {
			InputStream ims = ctx.getAssets().open("gfx/facebox.png");
		} catch (IOException e) {
		    //log the exception
			Log.v("IOError",e.getMessage());
		}
//		BufferedReader br = null;
//		AssetFileDescriptor descriptor;
//		try {
//			descriptor = BaseActivity.getSharedInstance().getAssets().openFd("sample.txt");
//			Log.v("mine",descriptor.toString());
//			FileReader reader = new FileReader(descriptor.getFileDescriptor());
//			br = new BufferedReader(reader);
//			try {
//		        StringBuilder sb = new StringBuilder();
//		        String line;
//				try {
//					line = br.readLine();
//					while (line != null) {
//			            sb.append(line);
//			            sb.append("\n");
//			            line = br.readLine();
//			        }
//			        String everything = sb.toString();
//			        System.out.println(everything);
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//
//		        
//		    } finally {
//		        try {
//					br.close();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//		    }
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			Log.v("mine", "json not read");
//			e.printStackTrace();
//		}
//	    
		JSONObject json= new JSONObject();
	}

	


}
