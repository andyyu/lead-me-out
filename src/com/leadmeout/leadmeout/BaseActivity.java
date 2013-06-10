package com.leadmeout.leadmeout;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.util.FPSLogger;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Typeface;
import android.util.Log;

public class BaseActivity extends SimpleBaseGameActivity {
	
	static final int CAMERA_WIDTH = 800;
	static final int CAMERA_HEIGHT = 480;

	public Font mFont;
	public Font mMenuFont;
	public Camera mCamera;
	public BitmapTextureAtlas mBitmapTextureAtlas;
	public BitmapTextureAtlas mLBitmapTextureAtlas;
	public BitmapTextureAtlas mRBitmapTextureAtlas;
	public BitmapTextureAtlas mPlayerBitmapTextureAtlas;
	public TiledTextureRegion mPathTile;
	public VertexBufferObjectManager vbo;
	public ITextureRegion mLeftArrow; 
	public ITextureRegion mRightArrow; 
	public ITextureRegion mUpArrow;
	public TiledTextureRegion mReset;
	public TiledTextureRegion mEraser;
	public TiledTextureRegion mDone;
	public ITiledTextureRegion mPlayer;
	public ITiledTextureRegion mDoor;
	public float playerX, playerY;
	public float endX,endY;
	

	//A reference to the current scene
	public Scene mCurrentScene;
	public static BaseActivity instance;
	
    @Override
    public EngineOptions onCreateEngineOptions() {
    	instance = this;
    	readLevel();
        mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
        
        return new EngineOptions(true, ScreenOrientation.LANDSCAPE_SENSOR, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), mCamera);

    }

	@Override
	protected void onCreateResources() {
		// TODO Auto-generated method stub
		loadGraphics();
		loadFonts();
		
	}
	
	public VertexBufferObjectManager getVBO(){
		return this.getVertexBufferObjectManager();
	}
	
	private void loadGraphics()
	{
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
    	
    	this.mBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR);
        
    	this.mLeftArrow= BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, this, "leftarrow.png", 0, 0, 1, 1);
        this.mRightArrow= BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, this, "rightarrow.png", 100, 0, 1, 1);
        this.mUpArrow= BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, this, "uparrow.png", 200, 0, 1, 1);
        
        this.mReset = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, this, "resetbutton.png", 0, 150, 1, 1);
        //this.mEraser = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, this, "rightarrow.png", 100, 0, 1, 1);
        this.mDone = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, this, "donebutton.png", 100, 150, 1, 1);
        
        this.mPlayer=BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, this, "facecircle.png", 300, 0, 1, 1);
        this.mDoor= BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, this, "door.png", 340, 0, 1, 1);
        
        this.mBitmapTextureAtlas.load();
    	
	}
	
	
	private void loadFonts()
	{
    	mFont = FontFactory.create(this.getFontManager(),this.getTextureManager(), 256, 256,Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 32);
    	mFont.load();
    	
    	mMenuFont = FontFactory.createFromAsset(this.getFontManager(), this.getTextureManager(), 256, 256, this.getAssets(),
    		    "fonts/therainmaker.otf", 80, true, android.graphics.Color.BLACK);
    	mMenuFont.load();
	}

	@Override
    protected Scene onCreateScene() {
        mEngine.registerUpdateHandler(new FPSLogger());
        mCurrentScene = new MainMenuScene();
        return mCurrentScene;
    }

	// to change the current main scene
    public void setCurrentScene(Scene scene) {
        mCurrentScene = scene;
        getEngine().setScene(mCurrentScene);
    }
    
    public static BaseActivity getSharedInstance() {
        return instance;
    }
    
    public void readLevel(){
		try {
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(getAssets().open("levels/sample.json")));

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
				JSONObject start= json.getJSONObject("start");
				Log.v("FilesLength", "Start: "+start.length());
				playerX=start.getInt("x");
				playerY=start.getInt("y");
				JSONObject end= json.getJSONObject("end");
				endX=end.getInt("x");
				endY=end.getInt("y");
				
				
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

}
