package com.aidaralfina.game;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.MotionEvent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.RectF;
import android.content.Context;
import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends Activity {
    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        setContentView(new GameView(this));
    }

    static class GameView extends View {
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        float heroX = 110, heroY = 0;
        float groundY;
        int hp = 5, defeated = 0;
        boolean left, right, attack;
        boolean won = false, lost = false;
        long lastAttack = 0;
        ArrayList<Enemy> enemies = new ArrayList<>();
        Random rng = new Random(7);
        float scale = 1;
        GameView(Context c) { super(c); for(int i=0;i<4;i++) enemies.add(new Enemy(400+i*210)); }
        void color(Canvas c, int col) { p.setColor(col); p.setStyle(Paint.Style.FILL); }
        void box(Canvas c, float l,float t,float r,float b,int col) { color(c,col); c.drawRoundRect(l,t,r,b,8,8,p); }
        void text(Canvas c,String s,float x,float y,int size,int col) {
            color(c,col); p.setTypeface(Typeface.create("sans-serif",Typeface.BOLD)); p.setTextSize(size); c.drawText(s,x,y,p);
        }
        @Override protected void onDraw(Canvas c) {
            super.onDraw(c);
            float w=getWidth(), h=getHeight(); groundY=h*0.76f;
            c.drawColor(Color.rgb(14,22,48));
            // twilight sky and distant castle
            color(c,Color.rgb(45,50,91)); c.drawRect(0,0,w,groundY,p);
            color(c,Color.rgb(74,62,112)); c.drawCircle(w*.82f,h*.18f,h*.13f,p);
            for(int i=0;i<7;i++){
                float x=w*.55f+i*w*.055f;
                color(c,Color.rgb(28,29,58));
                c.drawRect(x,groundY-h*(.15f+(i%3)*.04f),x+w*.055f,groundY,p);
                c.drawRect(x+w*.012f,groundY-h*(.22f+(i%2)*.05f),x+w*.04f,groundY-h*.13f,p);
                color(c,Color.rgb(255,178,93)); c.drawRect(x+w*.025f,groundY-h*.11f,x+w*.032f,groundY-h*.08f,p);
            }
            // forest floor
            color(c,Color.rgb(36,83,66)); c.drawRect(0,groundY,w,h,p);
            for(int i=0;i<18;i++){ color(c,Color.rgb(52,111,73)); c.drawCircle((i*137)%Math.max(1,(int)w),groundY+22+(i%3)*25,18,p); }
            // path
            color(c,Color.rgb(104,83,72)); c.drawRect(0,groundY+38,w,groundY+62,p);
            // goal: Alfina in castle gate
            box(c,w-110,groundY-130,w-38,groundY+4,Color.rgb(32,27,48));
            color(c,Color.rgb(255,206,127)); c.drawCircle(w-74,groundY-102,10,p);
            box(c,w-87,groundY-91,w-61,groundY-38,Color.rgb(248,210,224));
            text(c,"АЛЬФИНА",w-125,groundY-145,14,Color.WHITE);
            // enemies
            if(!won && !lost) {
                for(Enemy e:enemies) {
                    if(!e.dead) {
                        e.x -= 0.25f;
                        if(e.x < 20) e.x=w-180-rng.nextInt(160);
                        color(c,Color.rgb(75,173,91)); c.drawCircle(e.x,groundY-23,12,p);
                        box(c,e.x-10,groundY-12,e.x+10,groundY+10,Color.rgb(49,91,68));
                        color(c,Color.BLACK); c.drawCircle(e.x-4,groundY-25,2,p); c.drawCircle(e.x+4,groundY-25,2,p);
                        if(Math.abs(e.x-heroX)<28 && System.currentTimeMillis()%50==0) { hp--; if(hp<=0) lost=true; }
                    }
                }
            }
            // Aydar hero: dark hair, red cape, sword
            color(c,Color.rgb(153,39,59)); c.drawRoundRect(heroX-20,groundY-60,heroX+5,groundY-13,7,7,p);
            box(c,heroX-9,groundY-50,heroX+10,groundY-12,Color.rgb(34,39,57));
            color(c,Color.rgb(238,190,151)); c.drawCircle(heroX,groundY-66,12,p);
            color(c,Color.rgb(25,21,27)); c.drawArc(heroX-13,groundY-80,heroX+13,groundY-58,180,180,false,p);
            box(c,heroX-9,groundY-4,heroX-2,groundY+13,Color.rgb(24,26,36));
            box(c,heroX+3,groundY-4,heroX+10,groundY+13,Color.rgb(24,26,36));
            color(c,Color.rgb(218,224,235));
            if(attack || System.currentTimeMillis()-lastAttack<180) {
                p.setStrokeWidth(5); p.setStyle(Paint.Style.STROKE);
                c.drawArc(heroX+3,groundY-92,heroX+75,groundY-28,210,220,false,p); p.setStyle(Paint.Style.FILL);
                for(Enemy e:enemies) if(!e.dead && Math.abs(e.x-heroX)<82) { e.dead=true; defeated++; }
            } else {
                p.setStrokeWidth(4); c.drawLine(heroX+10,groundY-43,heroX+30,groundY-58,p);
            }
            // HUD
            box(c,14,12,210,54,Color.rgb(10,15,34));
            text(c,"АЙДАР",26,34,17,Color.WHITE);
            text(c,"❤ ".repeat(Math.max(0,hp)),26,49,15,Color.rgb(255,88,115));
            text(c,"Враги побеждены: "+defeated,230,34,16,Color.WHITE);
            text(c,"Цель: доберись до Альфины →",230,55,14,Color.rgb(255,225,166));
            // Touch controls
            box(c,18,h-82,92,h-16,Color.argb(180,8,14,31)); text(c,"◀",39,h-37,30,Color.WHITE);
            box(c,102,h-82,176,h-16,Color.argb(180,8,14,31)); text(c,"▶",122,h-37,30,Color.WHITE);
            box(c,w-112,h-88,w-18,h-16,Color.argb(210,112,30,56)); text(c,"УДАР",w-98,h-43,19,Color.WHITE);
            if(won || lost) {
                box(c,w*.22f,h*.3f,w*.78f,h*.65f,Color.rgb(10,15,34));
                text(c,won?"АЙДАР СПАС АЛЬФИНУ!":"АЙДАРУ НУЖНА ПОМОЩЬ",w*.28f,h*.43f,24,Color.WHITE);
                text(c,"Нажми в центре, чтобы начать заново",w*.29f,h*.53f,16,Color.rgb(255,211,224));
            }
            if(!won && heroX>w-155) won=true;
            if(left) heroX-=3.4f; if(right) heroX+=3.4f;
            heroX=Math.max(24,Math.min(w-130,heroX));
            postInvalidateDelayed(30);
        }
        @Override public boolean onTouchEvent(MotionEvent e) {
            float x=e.getX(), y=e.getY(), w=getWidth(), h=getHeight();
            if(e.getAction()==MotionEvent.ACTION_DOWN || e.getAction()==MotionEvent.ACTION_MOVE) {
                if((won||lost) && y>h*.3f && y<h*.7f) { heroX=100; hp=5; defeated=0; won=false; lost=false; enemies.clear(); for(int i=0;i<4;i++) enemies.add(new Enemy(400+i*210)); }
                left=x<95 && y>h-100; right=x>=95 && x<190 && y>h-100;
                attack=x>w-125 && y>h-105;
                if(attack) lastAttack=System.currentTimeMillis();
                return true;
            }
            if(e.getAction()==MotionEvent.ACTION_UP || e.getAction()==MotionEvent.ACTION_CANCEL) { left=false; right=false; attack=false; return true; }
            return true;
        }
        static class Enemy { float x; boolean dead=false; Enemy(float x){this.x=x;} }
    }
              }
