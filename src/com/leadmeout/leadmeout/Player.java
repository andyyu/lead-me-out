package com.leadmeout.leadmeout;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class Player extends GameObject{

	private Body body;
	public Rectangle sprite;
	public static Player instance;
	
	public Player(float pX, float pY, ITiledTextureRegion mPlayer,
			VertexBufferObjectManager pVertexBufferObjectManager, PhysicsWorld physicsWorld) {		
		super(pX, pY, mPlayer, pVertexBufferObjectManager);
		createPhysics(physicsWorld);
		// TODO Auto-generated constructor stub
	}

	public void moveLeft() {
        //this.mPhysicsHandler.setVelocityX(-100);
		body.setLinearVelocity(new Vector2(-5, body.getLinearVelocity().y));
    }
	public void moveRight() {
		body.setLinearVelocity(new Vector2(5, body.getLinearVelocity().y));
    }
	public void jump(){
		body.setLinearVelocity(new Vector2(body.getLinearVelocity().x, 30));
	}
	public void stop(){
		body.setLinearVelocity(new Vector2(0, body.getLinearVelocity().y));
	}
	
	private void createPhysics(PhysicsWorld physicsWorld)
	{		
		body = PhysicsFactory.createCircleBody(physicsWorld, this, BodyType.DynamicBody, PhysicsFactory.createFixtureDef(0, 0, 0));

		body.setUserData("player");
		body.setFixedRotation(true);

		physicsWorld.registerPhysicsConnector(new PhysicsConnector(this, body, true, false)
		{
			@Override
	        public void onUpdate(float pSecondsElapsed)
	        {
				super.onUpdate(pSecondsElapsed);
				//body.setLinearVelocity(new Vector2(5, body.getLinearVelocity().y));
	        }
		});
	}

}
