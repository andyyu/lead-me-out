package com.leadmeout.leadmeout;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.primitive.Line;
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
import org.andengine.util.color.Color;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
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
	int playerX, playerY;
	boolean gameOver;
	Rectangle floor;
	Context ctx;

	public GameScene(Context context) {
		ctx=context;
		activity=BaseActivity.getSharedInstance();
		setBackground(new Background(1.0f, 1.0f, 1.0f));
		mCamera=BaseActivity.getSharedInstance().mCamera;
		createPhysics();
		readLevel();
		createBackground();
		floorCollision();
		createHUD();		
		registerUpdateHandler(new IUpdateHandler() {
			@Override
			public void reset() { }

			@Override
			public void onUpdate(final float pSecondsElapsed) {

				//remove any sprites flagged for deletion
				try{
					removePlayer();
				}catch(Exception e)
				{
					Log.d("mine", "Exception removing objects from update:"+e);
				}
				catch(Error e)
				{
					Log.d("mine","Error removing objects from update:"+e);
				}

			}
		});
		setOnSceneTouchListener(this);
		setTouchAreaBindingOnActionDownEnabled(true);
	}	

	public void createPhysics(){
		physicsWorld = new FixedStepPhysicsWorld(60, new Vector2(0, 10), false); 
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

		floor= new Rectangle(0,480, 800, 1,BaseActivity.getSharedInstance().vbo);
		floor.setVisible(false);
		//PhysicsFactory.createBoxBody(this.physicsWorld, floor, BodyType.StaticBody, wallFixtureDef);
		attachChild(floor);
	}

	public void createPlayer(int x, int y){
		playerX=x;
		playerY=y;
		player=new Player(x, y, BaseActivity.getSharedInstance().mPlayer, BaseActivity.getSharedInstance().getVertexBufferObjectManager(), physicsWorld);
		attachChild(player);
	}

	public void createEnd(int x, int y){

	}

	public void createHUD(){
		final Sprite leftButton = new ButtonSprite(500, 350, activity.mLeftArrow, activity.mLeftArrow, activity.mLeftArrow, activity.getVertexBufferObjectManager()){
			@Override
			public boolean onAreaTouched(TouchEvent pTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if(player!=null){
					if(pTouchEvent.isActionDown()) {
						player.moveLeft();
						Log.v("mine","left move");
					}
					if(pTouchEvent.isActionUp()){
						player.stop();
					}
				}
				return super.onAreaTouched(pTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
			}
		};
		final Sprite rightButton = new ButtonSprite(600, 350, activity.mRightArrow, activity.mRightArrow, activity.mRightArrow, activity.getVertexBufferObjectManager()){
			@Override
			public boolean onAreaTouched(TouchEvent pTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if(player!=null){
					if(pTouchEvent.isActionDown()) {
						player.moveRight();
						Log.v("mine","right move");
					}
					if(pTouchEvent.isActionUp()){
						player.stop();
					}
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
				if(player!=null){
					if(player.collidesWith(floor)) {						
						gameOver=true;
						setBackground(new Background(1.0f, 0.0f, 0.2f));
						detachChild(player);
					} 
				}
			}
		});

	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		// TODO Auto-generated method stub
		if(pSceneTouchEvent.isActionDown()) {
			if(player!=null){
				player.jump();
				Log.v("mine","right move");
			}
			if(gameOver){
				Log.v("mine", "game over");
				setBackground(new Background(1,1,1));
				createPlayer(playerX,playerY);
				gameOver=false;
			}

		}
		if(pSceneTouchEvent.isActionUp()){
			if(player!=null){
				player.stop();
			}
		}
		return false;
	}

	public void readLevel(){
		final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0, 0.2f, 10f);
		try {
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(ctx.getAssets().open("levels/sample.json")));

			// do reading, usually loop until end of file reading
			String all="";
			String mLine = reader.readLine();
			while (mLine != null) {
				//process line
				all+=mLine;
				Log.v("FilesLength", mLine);
				mLine = reader.readLine(); 
			}
			reader.close();
			try {
				JSONObject json= new JSONObject(all);
				JSONArray lines= json.getJSONArray("lines");
				Log.v("FilesLength", "lines:"+lines.join(","));
				for(int i=0;i<lines.length();i++){
					if(lines.getJSONObject(i)!=null){
						JSONObject line= lines.getJSONObject(i);
						int x1= line.getInt("x1");
						int x2= line.getInt("x2");
						int y1= line.getInt("y1");
						int y2= line.getInt("y2");
						makePlatform(wallFixtureDef,x1,y1,x2,y2);
					}
				}
				JSONObject start= json.getJSONObject("start");
				Log.v("FilesLength", "Start: "+start.length());
				createPlayer(start.getInt("x"), start.getInt("y"));
				JSONObject end= json.getJSONObject("end");
				createEnd(end.getInt("x"), end.getInt("y"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				Log.v("JSONException", e.getMessage());
				e.printStackTrace();
			}
		} catch (IOException e) {
			//log the exception
			Log.v("IOError",e.getMessage());
		}


	}

	public void makePlatform(FixtureDef fixtureDef, int x1, int y1, int x2, int y2){
		String str= "("+x1+","+y1+")("+x2+","+y2+")";
		Line line= new Line(x1, y1, x2, y2, 5, BaseActivity.getSharedInstance().vbo);
		line.setColor(new Color(0,0,0));
		PhysicsFactory.createLineBody(this.physicsWorld, line,fixtureDef);
		attachChild(line);
	}

	public void removePlayer(){
		if(gameOver){
			final Body body= player.body;
			physicsWorld.unregisterPhysicsConnector(physicsWorld.getPhysicsConnectorManager().findPhysicsConnectorByShape(player));
			physicsWorld.destroyBody(body);
			detachChild(player);
		}
	}






}
