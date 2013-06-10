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
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
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
	float playerX, playerY;
	float endX, endY;
	boolean gameOver;
	Rectangle floor;
	Sprite end;
	Rectangle door;
	Context ctx;
	String jsonStr;
	final FixtureDef wallFixtureDef;

	public GameScene(Context context, String s) {
		wallFixtureDef = PhysicsFactory.createFixtureDef(0, 0.2f, 10f);
		jsonStr=s;
		ctx=context;
		activity=BaseActivity.getSharedInstance();
		setBackground(new Background(1.0f, 1.0f, 1.0f));
		mCamera=BaseActivity.getSharedInstance().mCamera;
		playerX=BaseActivity.getSharedInstance().playerX;
		playerY=BaseActivity.getSharedInstance().playerY;
		endX=BaseActivity.getSharedInstance().endX;
		endY=BaseActivity.getSharedInstance().endY;
		createPhysics();
		readLevel();		
		createBackground();
		recreatePlayer();
		createEnd();
		collisions();
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
		
		Rectangle wall1= new Rectangle(0,0, 1, 480,BaseActivity.getSharedInstance().vbo);
		PhysicsFactory.createBoxBody(this.physicsWorld, wall1, BodyType.StaticBody, wallFixtureDef);
		Rectangle wall2= new Rectangle(799,0, 800, 480,BaseActivity.getSharedInstance().vbo);
		PhysicsFactory.createBoxBody(this.physicsWorld, wall2, BodyType.StaticBody, wallFixtureDef);
		
		attachChild(floor);
		attachChild(wall1);
		attachChild(wall2);
	}

	public void recreatePlayer(){
		player=new Player(playerX, playerY, BaseActivity.getSharedInstance().mPlayer, BaseActivity.getSharedInstance().getVertexBufferObjectManager(), physicsWorld);
		attachChild(player);
	}

	public void createEnd(){
		end= new Sprite(endX,endY,activity.mDoor,activity.getVertexBufferObjectManager());
		attachChild(end);
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

	public void collisions(){

		registerUpdateHandler(new IUpdateHandler() {
			@Override
			public void reset() { }

			@Override
			public void onUpdate(final float pSecondsElapsed) {
				if(player!=null && !gameOver){
					if(player.collidesWith(floor)) {						
						gameOver=true;
						setBackground(new Background(1.0f, 0.0f, 0.2f));
						detachChild(player);
					} 
				}
				if(player!=null && !gameOver){
					if(player.collidesWith(end)) {	
						setBackground(new Background(0.2f, 1.0f, 0.0f));
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
			if(!gameOver && player==null){
				Log.v("mine", "game over");
				setBackground(new Background(1,1,1));
				recreatePlayer();
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
			try {
				JSONObject json=new JSONObject(jsonStr);
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
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				Log.v("JSONException", e.getMessage());
				e.printStackTrace();
			}

	}

	public void makePlatform(FixtureDef fixtureDef, float x1,  float y1,  float x2,  float y2){
		String str= "("+x1+","+y1+")("+x2+","+y2+")";
		Line line= new Line(x1, y1, x2, y2, 5, BaseActivity.getSharedInstance().vbo);
		line.setColor(new Color(0,0,0));
		PhysicsFactory.createLineBody(this.physicsWorld, line,fixtureDef);
		attachChild(line);
	}

	public void removePlayer(){
		if(gameOver){
			gameOver=false;
			Log.v("FilesLength","remove Called");
			player.setVisible(false);
			player.detachSelf();
			player.clearUpdateHandlers();
			physicsWorld.unregisterPhysicsConnector(
			        physicsWorld.getPhysicsConnectorManager().findPhysicsConnectorByShape(player));
			physicsWorld.destroyBody(player.body);
			player=null;
		}
	}






}
