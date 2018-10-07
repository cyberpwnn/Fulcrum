package com.volmit.fulcrum.resourcepack;

public enum CompressionMode
{
	/**
	 * Literally writes the pack into a folder without compressing images, json or
	 * anything
	 */
	RAW,

	/**
	 * Minifies the json texts but does not compress images or the pack itself
	 */
	MINIFIED_RAW,

	/**
	 * Lightly compresses the pack (zip) but does not minify json
	 */
	COMPRESS,

	/**
	 * Compresses the pack with a level of 4, minifies json but does not compress
	 * images
	 */
	PRODUCTION,

	/**
	 * Compresses the pack with a level of 9 (max), minifies and oversimplifies +
	 * deduplicates json, and finally optimizes png images by dithering and
	 * resampling at lower bit depths.
	 */
	EDGY;
}
