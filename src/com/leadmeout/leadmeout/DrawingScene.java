package com.leadmeout.leadmeout;

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
import org.andengine.entity.text.Text;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.color.Color;
import org.andengine.util.modifier.IModifier;

import android.graphics.Path;
import android.hardware.SensorManager;

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

	Line bodyLine;
	Body lb;
	Line line;
	Text centerText;
	DelayModifier dMod;
	Rectangle[] rec = new Rectangle[250];

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
		//activity.grav = 0;
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

		attachChild(ground);
		attachChild(roof);
		attachChild(left);
		attachChild(right);
	}
	
	public boolean onSceneTouchEvent(final Scene pScene, final TouchEvent pSceneTouchEvent) {
		final VertexBufferObjectManager vertexBufferObjectManager = activity.getVertexBufferObjectManager();


		// centerText.detachSelf();

		if(linesDrawn <= 1000){
			if(pSceneTouchEvent.getAction() == TouchEvent.ACTION_DOWN) {
				isDrawing = true;
				i = 0;
			}
			if(pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP) {
				isDrawing = false;
			}
			if (isDrawing = true) {
				rec[i] = new Rectangle(pSceneTouchEvent.getX(), pSceneTouchEvent.getY(), 1, 1, vertexBufferObjectManager);
				if (i != 0) {
					Line l = new Line(rec[i-1].getX(), rec[i-1].getY(), rec[i].getX(), rec[i].getY(), null);
					Line bodyLine = new Line(rec[i-1].getX(), rec[i-1].getY(), rec[i].getX(), rec[i].getY(), null);
					Body lb;
					Line line = new Line (rec[i-1].getX(), rec[i-1].getY(), rec[i].getX(), rec[i].getY(), null);
					lb = PhysicsFactory.createLineBody(mPhysicsWorld, bodyLine,
							wallFixtureDef);
					lb.setUserData("line");
					bodyLine.setVisible(false);
					line.setColor(Color.BLACK);
					bodyLine.setColor(255, 0, 0);
					line.setLineWidth(5);
					attachChild(bodyLine);
					attachChild(line);
					linesDrawn ++;

				}
				linesDrawn ++;
				i++;

			}
		}
		return true;


	}
}
