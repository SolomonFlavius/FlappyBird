package com.wilwers.flappybird;




import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;


public class Game extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
	//ShapeRenderer shapeRenderer;

	Texture gameover;

	Texture[] birds;
	int flapState = 0;
	float birdY = 0;
	float velocity = 0;
	Circle birdCircle;
	int score = 0;
	int scoringTube = 0;
	BitmapFont font;

	int gameState = 0;
	float gravity = 1.5f;

	Texture topTube;
	Texture bottomTube;
	float gap = 400;
	float maxTubeOffset;
	Random randomGenerator;
	float tubeVelocity = 4;
	int numberOfTubes = 4;
	float[] tubeX = new float[numberOfTubes];
	float[] tubeOffset = new float[numberOfTubes];
	float distanceBetweenTubes;
	Rectangle[] topTubeRectangles;
	Rectangle[] bottomTubeRectangles;

	//highscore
	Preferences prefs;
	int highscore;

	Sound wing;
	Sound point;
	Sound hit;
	boolean hitWasPlayed;



	@Override
	public void create () {
		hitWasPlayed = false;
		wing = Gdx.audio.newSound(Gdx.files.internal("wing.mp3"));
		point = Gdx.audio.newSound(Gdx.files.internal("point.mp3"));
		hit = Gdx.audio.newSound(Gdx.files.internal("hit.mp3"));
		prefs = Gdx.app.getPreferences("gamePrefs");
		highscore = prefs.getInteger("highscore");
		batch = new SpriteBatch();
		background = new Texture("bg.png");
		gameover = new Texture("gameover.png");
		//shapeRenderer = new ShapeRenderer();
		birdCircle = new Circle();
		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(10);

		birds = new Texture[2];
		birds[0] = new Texture("bird.png");
		birds[1] = new Texture("bird2.png");


		topTube = new Texture("toptube.png");
		bottomTube = new Texture("bottomtube.png");
		maxTubeOffset = Gdx.graphics.getHeight() / 2f - gap / 2 - 100;
		randomGenerator = new Random();
		distanceBetweenTubes = Gdx.graphics.getWidth() * 3 / 4f;
		topTubeRectangles = new Rectangle[numberOfTubes];
		bottomTubeRectangles = new Rectangle[numberOfTubes];


		startGame();





	}

	public void startGame() {
		hitWasPlayed = false;

		birdY = Gdx.graphics.getHeight() / 2f - birds[0].getHeight() / 2f;

		for (int i = 0; i < numberOfTubes; i++) {

			tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);

			tubeX[i] = Gdx.graphics.getWidth() / 2f - topTube.getWidth() / 2f + Gdx.graphics.getWidth() + i * distanceBetweenTubes;

			topTubeRectangles[i] = new Rectangle();
			bottomTubeRectangles[i] = new Rectangle();

		}

	}

	@Override
	public void render () {

		batch.begin();
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		if (gameState == 1)
		{

			if (tubeX[scoringTube] < Gdx.graphics.getWidth() / 2f) {

				score++;
				point.play();
				if (score > highscore)
				{
					highscore = score;
					prefs.putInteger("highscore", highscore);
					prefs.flush();
				}

				//Gdx.app.log("Score", String.valueOf(score));

				if (scoringTube < numberOfTubes - 1) {

					scoringTube++;

				} else {

					scoringTube = 0;

				}

			}

			if (Gdx.input.justTouched()) {
				flapState = 1;
				velocity = -25;
				wing.play();

			}
			else
			{
				flapState = 0;
			}

			for (int i = 0; i < numberOfTubes; i++) {

				if (tubeX[i] < - topTube.getWidth()) {

					tubeX[i] += numberOfTubes * distanceBetweenTubes;
					tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);

				} else {

					tubeX[i] = tubeX[i] - tubeVelocity;



				}

				batch.draw(topTube, tubeX[i], Gdx.graphics.getHeight() / 2f + gap / 2 + tubeOffset[i]);
				batch.draw(bottomTube, tubeX[i], Gdx.graphics.getHeight() / 2f - gap / 2 - bottomTube.getHeight() + tubeOffset[i]);

				topTubeRectangles[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2f + gap / 2 + tubeOffset[i], topTube.getWidth(), topTube.getHeight());
				bottomTubeRectangles[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2f - gap / 2 - bottomTube.getHeight() + tubeOffset[i], bottomTube.getWidth(), bottomTube.getHeight());
			}

			if(birdY > Gdx.graphics.getHeight() - birds[0].getHeight())
			{
				hit.play();
				gameState = 2;
			}
			else if (birdY > 0)
			{
				velocity = velocity + gravity;
				birdY -= velocity;

			}
			else
			{
				hit.play();
				gameState = 2;

			}

		} else if (gameState == 0) {

			if (Gdx.input.justTouched())
			{
				gameState = 1;
			}

		} else if (gameState == 2) {

			if(birdY > birds[0].getHeight() / 2f)
			{
				velocity = velocity + gravity;
				birdY -= velocity;
			}

			batch.draw(gameover, Gdx.graphics.getWidth() / 2f - gameover.getWidth() / 2f, Gdx.graphics.getHeight() / 1.25f );

			font.draw(batch,"Highscore:" + highscore, Gdx.graphics.getWidth() / 2f - gameover.getWidth() / 2f, Gdx.graphics.getHeight() / 1.25f - 20);

			if (Gdx.input.justTouched()) {

				gameState = 1;
				startGame();
				score = 0;
				scoringTube = 0;
				velocity = 0;


			}

		}




		batch.draw(birds[flapState], Gdx.graphics.getWidth() / 2f - birds[flapState].getWidth() / 2f, birdY);

		font.draw(batch, String.valueOf(score), 100, 200);

		birdCircle.set(Gdx.graphics.getWidth() / 2f, birdY + birds[flapState].getHeight() / 2f, birds[flapState].getWidth() / 2f);



		//shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		//shapeRenderer.setColor(Color.RED);
		//shapeRenderer.circle(birdCircle.x, birdCircle.y, birdCircle.radius);

		for (int i = 0; i < numberOfTubes; i++) {

			//shapeRenderer.rect(tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i], topTube.getWidth(), topTube.getHeight());
			//shapeRenderer.rect(tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i], bottomTube.getWidth(), bottomTube.getHeight());


			if (Intersector.overlaps(birdCircle, topTubeRectangles[i]) || Intersector.overlaps(birdCircle, bottomTubeRectangles[i]))
			{
				if(!hitWasPlayed)
				hit.play();
				hitWasPlayed=true;
				gameState = 2;
				break;
			}

		}

		batch.end();

		//shapeRenderer.end();



	}


}
