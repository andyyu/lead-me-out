package com.leadmeout.leadmeout;

import java.util.ArrayList;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.primitive.Line;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.color.Color;
import org.andengine.util.modifier.IModifier;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Path;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.Toast;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;

public class DrawingScene extends Scene implements IOnSceneTouchListener {
	// ===========================================================
	// Constants
	// ===========================================================


	private Scene mScene;

	static PhysicsWorld mPhysicsWorld;

	private Path path = new Path();
	BaseActivity activity;
	DrawingScene scene;
	static AnimatedSprite player;
	PhysicsWorld bPhysicsWorld;

	private int i = 0;
	int x;
	private boolean isDrawing = false;

	int linesDrawn = 0;
	Body body;
	boolean destroy = false;

	FixtureDef FIXTURE_DEF = PhysicsFactory.createFixtureDef(1, 0.5f, 0.5f);
	FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0, 0.5f, 0.5f);
	protected MenuScene menu;

	Color linecolor=new Color (0,0,0);
	int linewidth = 5;
	Line bodyLine;
	Body lb;
	Line line;
	Text centerText;
	DelayModifier dMod;
	ArrayList<Rectangle> rec = new ArrayList<Rectangle>();

	public DrawingScene(){

		final DelayModifier dMod = new DelayModifier(2, new IEntityModifierListener() {
			@Override
			public void onModifierStarted(IModifier arg0, IEntity arg1) {
			}
			public void onModifierFinished(IModifier arg0, IEntity arg1) {
			}
		});

		setBackground(new Background(1, 1, 1));
		setOnSceneTouchListener(this);
		activity = BaseActivity.getSharedInstance();
		Camera mCamera = activity.mCamera;

		mPhysicsWorld = new PhysicsWorld(new Vector2(0, SensorManager.GRAVITY_EARTH), false);
		final VertexBufferObjectManager vertexBufferObjectManager = activity.getVertexBufferObjectManager();
		final Rectangle ground = new Rectangle(0, mCamera.getHeight() - 2,  mCamera.getWidth(), 2, vertexBufferObjectManager);
		final Rectangle roof = new Rectangle(0, 0,  mCamera.getWidth(), 2, vertexBufferObjectManager);
		final Rectangle left = new Rectangle(0, 0, 2,  mCamera.getHeight(), vertexBufferObjectManager);
		final Rectangle right = new Rectangle( mCamera.getWidth() - 2, 0, 2,  mCamera.getHeight(), vertexBufferObjectManager);

		ground.setColor(0, 255, 255);

		Body Ground = PhysicsFactory.createBoxBody(mPhysicsWorld, ground, BodyType.StaticBody, wallFixtureDef);
		Body Roof = PhysicsFactory.createBoxBody(mPhysicsWorld, roof, BodyType.StaticBody, wallFixtureDef);
		Body Left = PhysicsFactory.createBoxBody(mPhysicsWorld, left, BodyType.StaticBody, wallFixtureDef);
		Body Right = PhysicsFactory.createBoxBody(mPhysicsWorld, right, BodyType.StaticBody, wallFixtureDef);

		Ground.setUserData("ground");
		Roof.setUserData("wall");
		Left.setUserData("wall");
		Right.setUserData("wall");
		createHUD();

		attachChild(ground);
		attachChild(roof);
		attachChild(left);
		attachChild(right);
	}

	public void createHUD(){

		final Sprite doneButton = new ButtonSprite(500, 350, activity.mDone, activity.mDone, activity.mDone, activity.getVertexBufferObjectManager()){
			@Override
			public boolean onAreaTouched(TouchEvent pTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if(pTouchEvent.isActionDown()) {
					final JSONObject drawing = new JSONObject();
					JSONArray lines = new JSONArray();
					for (int i = 0; i< rec.size()-1; i++)
					{
						JSONObject line = new JSONObject();
						try {
							if (rec.get(i)!=null && rec.get(i+1)!=null)
							{
								line.put("x1", rec.get(i).getX());
								line.put("y1", rec.get(i).getY());
								line.put("x2", rec.get(i+1).getX());
								line.put("y2", rec.get(i+1).getY());
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						lines.put(line);
					}
					Log.v("mine",""+lines.length());
					try {
						drawing.put("drawing", lines);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					BaseActivity.getSharedInstance().runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(BaseActivity.getSharedInstance(), 
									drawing.toString(), Toast.LENGTH_LONG).show();
						}
					});
				}

				BaseActivity.getSharedInstance().setCurrentScene(new GameScene(BaseActivity.getSharedInstance()));
				return super.onAreaTouched(pTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
			}

		};
		final Sprite resetButton = new ButtonSprite(600, 350, activity.mReset, activity.mReset, activity.mReset, activity.getVertexBufferObjectManager()){
			@Override
			public boolean onAreaTouched(TouchEvent pTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if(pTouchEvent.isActionDown()) {
					Log.v("mine", "resettouched");
					activity.setCurrentScene(new DrawingScene());
				}
				return super.onAreaTouched(pTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
			}
		};
		/**
		final Sprite eraseButton = new ButtonSprite(600, 350, activity.mRightArrow, activity.mRightArrow, activity.mRightArrow, activity.getVertexBufferObjectManager()){
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
		 **/
		registerTouchArea(doneButton);
		attachChild(doneButton);	
		registerTouchArea(resetButton);
		attachChild(resetButton);
	}

	public boolean onSceneTouchEvent(final Scene pScene, final TouchEvent pSceneTouchEvent) {
		final VertexBufferObjectManager vertexBufferObjectManager = activity.getVertexBufferObjectManager();


		// centerText.detachSelf();

		if(linesDrawn <= 1000){
			if(pSceneTouchEvent.getAction() == TouchEvent.ACTION_DOWN) {
				isDrawing = true;
			}
			if(pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP) {
				isDrawing = false;

			}
			if (isDrawing = true) {
				rec.add(new Rectangle(pSceneTouchEvent.getX(), pSceneTouchEvent.getY(), 1, 1, vertexBufferObjectManager));
				if (i != 0) {
					Line l = new Line(rec.get(i-1).getX(), rec.get(i-1).getY(), rec.get(i).getX(), rec.get(i).getY(), null);
					Line bodyLine = new Line(rec.get(i-1).getX(), rec.get(i-1).getY(), rec.get(i).getX(), rec.get(i).getY(), null);
					Body lb;
					Line line = new Line (rec.get(i-1).getX(), rec.get(i-1).getY(), rec.get(i).getX(), rec.get(i).getY(), null);
					lb = PhysicsFactory.createLineBody(mPhysicsWorld, bodyLine,
							wallFixtureDef);
					lb.setUserData("line");
					bodyLine.setVisible(false);
					line.setColor(linecolor);
					bodyLine.setColor(255, 0, 0);
					line.setLineWidth(linewidth);
					attachChild(bodyLine);
					attachChild(line);
					linesDrawn ++;

				}
				i++;

			}
		}
		return true;


	}
}
