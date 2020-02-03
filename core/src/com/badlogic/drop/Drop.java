package com.badlogic.drop;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

public class Drop extends ApplicationAdapter {
	private Texture alienImage;
	private Texture truckImage;
	private Texture splashImage;

	private OrthographicCamera camera;
	private SpriteBatch batch;

	private Rectangle truck;
	private Rectangle water;

	private Array<Rectangle> aliens;
	private long lastDropTime;

	private int score;
	private String scoreName;
	private BitmapFont bitmapFontName;

	private boolean isAttack;

	@Override
	public void create () {
		//load images for droplet and bucket
		alienImage = new Texture(Gdx.files.internal("alien.png"));
		truckImage = new Texture(Gdx.files.internal("truck.png"));
		splashImage = new Texture(Gdx.files.internal("waterSplash.png"));

		//set score to 0
		score = 0;
		scoreName = "Score: 0";
		bitmapFontName = new BitmapFont();

		camera = new OrthographicCamera();
		camera.setToOrtho(false, 1600, 960);

		batch = new SpriteBatch();

		truck = new Rectangle();
		truck.x = 1600 / 2 - 120 / 2;
		truck.y = 20;
		truck.width = 94;
		truck.height = 172;

		water = new Rectangle();
		water.x = truck.x;
		water.y= truck.y + 150;
		water.width = 45;
		water.height = 230;

		aliens = new Array<Rectangle>();
		spawnAlien();

	}

	private void spawnAlien() {
		Rectangle alien = new Rectangle();
		alien.x = MathUtils.random(0, 1600 - 210);
		alien.y = 960;
		alien.width = 105;
		alien.height = 210;
		aliens.add(alien);
		lastDropTime = TimeUtils.millis();
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		camera.update();

		batch.setProjectionMatrix(camera.combined);

		batch.begin();
		batch.draw(truckImage, truck.x, truck.y);
		bitmapFontName.setColor(1.0f,1.0f,1.0f, 1.0f);
		bitmapFontName.draw(batch, scoreName, 25, 100);
		for (Rectangle alien : aliens) {
			batch.draw(alienImage, alien.x, alien.y);
		}

		if (isAttack){
			batch.draw(splashImage, water.x, water.y);
		}
		batch.end();

		if (Gdx.input.isTouched()) {
			Vector3 touchPos = new Vector3();
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchPos);
			truck.x = touchPos.x - 94 / 2;
			water.x=truck.x;
			isAttack = true;
		} else { isAttack = false;}

		if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) truck.x -= 200 * Gdx.graphics.getDeltaTime();
		if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) truck.x += 200 * Gdx.graphics.getDeltaTime();

		if (truck.x < 0) truck.x = 0;
		if (truck.x > 1600 - 94) truck.x = 1600 - 94;

		if (TimeUtils.millis() - lastDropTime > 2000) spawnAlien();

		for (Iterator<Rectangle> iter = aliens.iterator(); iter.hasNext(); ) {
			Rectangle alien = iter.next();
			alien.y -= (MathUtils.random(100, 200)) * Gdx.graphics.getDeltaTime();
			if (alien.y + 210 < 0) {
				iter.remove();
				score --;
				scoreUpdate();
			}
			if (alien.overlaps(truck)) {
				iter.remove();
				score = score - 2;
				scoreUpdate();
			}
			if (alien.overlaps(water)){
				if (isAttack){
					iter.remove();
					score ++;
					scoreUpdate();
			}}
		}
	}

	private void scoreUpdate(){
		scoreName="Score: " + score;
	}

	@Override
	public void dispose () {
			alienImage.dispose();
			truckImage.dispose();
			batch.dispose();
		}

}
