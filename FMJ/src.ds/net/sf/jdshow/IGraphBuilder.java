package net.sf.jdshow;

/**
 * 
 * @author Ken Larson
 *
 */
public class IGraphBuilder extends IFilterGraph
{

	public IGraphBuilder(long ptr)
	{
		super(ptr);
		
	}
	
	public native int RenderFile(String file, String playlist);

    static native GUID Init_IID(GUID guid);
}
