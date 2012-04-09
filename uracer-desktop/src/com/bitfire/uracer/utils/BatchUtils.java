package com.bitfire.uracer.utils;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.bitfire.uracer.Art;

public final class BatchUtils {
	private static String[] chars = { "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789", ".,!?:;\"'+-=/\\< " };

	private BatchUtils() {
	}

	public static void draw( SpriteBatch batch, TextureRegion region, float x, float y ) {
		int width = region.getRegionWidth();
		if( width < 0 ) {
			width = -width;
		}

		batch.draw( region, x, y, width, -region.getRegionHeight() );
	}

	public static void draw( SpriteBatch batch, TextureRegion region, float x, float y, float width, float height ) {
		batch.draw( region, x, y, width, height );
	}

	public static void drawString( SpriteBatch batch, String string, float x, float y ) {
		String upstring = string.toUpperCase();
		for( int i = 0; i < string.length(); i++ ) {
			char ch = upstring.charAt( i );
			for( int ys = 0; ys < chars.length; ys++ ) {
				int xs = chars[ys].indexOf( ch );
				if( xs >= 0 ) {
					draw( batch, Art.base6[xs][ys + 9], x + i * 6, y );
				}
			}
		}
	}

	public static void drawString( SpriteBatch batch, String string, float x, float y, float w, float h ) {
		String upstring = string.toUpperCase();
		for( int i = 0; i < string.length(); i++ ) {
			char ch = upstring.charAt( i );
			for( int ys = 0; ys < chars.length; ys++ ) {
				int xs = chars[ys].indexOf( ch );
				if( xs >= 0 ) {
					draw( batch, Art.base6[xs][ys + 9], x + i * w, y, w, h );
				}
			}
		}
	}

}