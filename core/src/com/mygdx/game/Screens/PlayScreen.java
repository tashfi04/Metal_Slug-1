package com.mygdx.game.Screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TideMapLoader;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthoCachedTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.Scene.Hud;
import com.mygdx.game.Sprites.Enemies.Sniper;
import com.mygdx.game.Sprites.Player;
import com.mygdx.game.Tools.B2WorldCreator;
import com.mygdx.game.Tools.WorldContactListener;


public class PlayScreen implements Screen {

    private long countDt=0;
    private float timeCount=0;
    private long Timer=1;

    private MyGdxGame game;

    //public Warrior player;
    public Player player;

    public boolean bool;
    public boolean isHurt=false;
    public boolean enemyisHurt=false;
    public boolean cam = false;

    public Sniper sniper;

    public int k=1;




    private OrthographicCamera gamecam;
    private Viewport gamePort;
    private Hud hud;

    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    //Box2d variables
    private World world;
    private Box2DDebugRenderer b2dr;





    public PlayScreen(MyGdxGame game)
    {
        this.game=game;

        gamecam= new OrthographicCamera();
        gamePort= new FitViewport(MyGdxGame.V_Width,MyGdxGame.V_Height,gamecam);
        hud=new Hud(game.batch);
        mapLoader =new TmxMapLoader();
        map=mapLoader.load("Map/map.tmx");
        renderer= new OrthogonalTiledMapRenderer(map);
        gamecam.position.set(gamePort.getWorldWidth()/2,gamePort.getWorldHeight()/2,0);
        bool=false;
        world =new World (new Vector2(0,-150),true);
        b2dr= new Box2DDebugRenderer();
        new B2WorldCreator(world,map);
        //player= new Warrior(world,this);
        player = new Player(world, this);
        // world.setContactListener(new WorldContactListener());

        sniper = new Sniper(world, this, 600, 130);

    }


    @java.lang.Override
    public void show() {

    }
    public void handleInput(float dt) {
        timeCount+=dt;
        if(timeCount>=1){
            Timer++;
            timeCount=0;
        }
        countDt++;
        countDt=countDt%500;
        if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE))
            game.setScreen(game.menuScreen);

        if(Gdx.input.isKeyJustPressed(Input.Keys.UP))
            player.b2body.applyLinearImpulse(new Vector2(0, 153f), player.b2body.getWorldCenter(), true);

        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT) && player.b2body.getLinearVelocity().x<=50)
            player.b2body.applyLinearImpulse(new Vector2(50f,0),player.b2body.getWorldCenter(),true);

        if(Gdx.input.isKeyPressed(Input.Keys.LEFT) && player.b2body.getLinearVelocity().x>=-50)
            player.b2body.applyLinearImpulse(new Vector2(-50f,0),player.b2body.getWorldCenter(),true);

    }
    public void update(float dt){
        handleInput(dt);
        world.step(1/60f,6,2);

        //player
        player.update(dt);

        //enemy
        sniper.update(dt, player.b2body.getPosition().x);

        //System.out.println(enemy.b2body.getPosition().x);
        // System.out.println(player.b2body.getPosition().x);

        for(int i = 0;i < player.bullets.size() ; i++){
            player.bullets.get(i).update(dt);
        }

        hud.update(dt);

        gamecam.position.x= player.b2body.getPosition().x+80;
        if(player.b2body.getPosition().x > 3306 && player.b2body.getPosition().x < 3542) // 3306 3542)
            gamecam.position.y = player.b2body.getPosition().y+40;


        //System.out.println(player.b2body.getPosition().x);



        gamecam.update();
        renderer.setView(gamecam);
    }
    @java.lang.Override
    public void render(float delta) {
        update(delta);

        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);



        renderer.render();
        //render our box debug line
        b2dr.render(world,gamecam.combined);
        game.batch.setProjectionMatrix(gamecam.combined);
        game.batch.begin();

        player.draw(game.batch);

        sniper.draw(game.batch);

        game.batch.end();

        for(int i = 0 ; i < player.bullets.size() ; i++){
            player.bullets.get(i).draw(game.batch);
        }


        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();



    }

    @java.lang.Override
    public void resize(int width, int height) {
        gamePort.update(width,height);
    }

    @java.lang.Override
    public void pause() {

    }

    @java.lang.Override
    public void resume() {

    }

    @java.lang.Override
    public void hide() {

    }

    @java.lang.Override
    public void dispose() {
        map.dispose();
        renderer.dispose();
        world.dispose();
        b2dr.dispose();
        hud.dispose();

    }
}
