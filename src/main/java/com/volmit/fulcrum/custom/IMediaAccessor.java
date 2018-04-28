package com.volmit.fulcrum.custom;

import java.net.URL;

public interface IMediaAccessor extends ICustom
{
	public URL access(String path);
}
