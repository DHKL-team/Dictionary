package STT.util;

//TODO Replace this class with something that isn't 20 years old.

//ChunkedOutputStream - an OutputStream that implements HTTP/1.1 chunking
//
//Copyright (C) 1996 by Jef Poskanzer <jef@acme.com>.  All rights reserved.
//
//Redistribution and use in source and binary forms, with or without
//modification, are permitted provided that the following conditions
//are met:
//1. Redistributions of source code must retain the above copyright
// notice, this list of conditions and the following disclaimer.
//2. Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
//
//THIS SOFTWARE IS PROVIDED BY THE AUTHOR AND CONTRIBUTORS ``AS IS'' AND
//ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
//IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
//ARE DISCLAIMED.  IN NO EVENT SHALL THE AUTHOR OR CONTRIBUTORS BE LIABLE
//FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
//DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
//OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
//HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
//LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
//OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
//SUCH DAMAGE.
//
//Visit the ACME Labs Java page for up-to-date versions of this and other
//fine Java utilities: http://www.acme.com/java/


import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class ChunkedOutputStream extends BufferedOutputStream
{

	private static final byte[] crlf = { 13, 10 };
	private byte[] lenBytes = new byte[20]; // big enough for any number in hex
	private List<String> footerNames = new ArrayList<String>();
	private List<String> footerValues = new ArrayList<String>();

	public ChunkedOutputStream( OutputStream out )
	{
		super( out );
	}

	public ChunkedOutputStream( OutputStream out, int size )
	{
		super( out, size );
	}

	public synchronized void flush() throws IOException
	{
		if ( count != 0 )
		{
			writeBuf( buf, 0, count );
			count = 0;
		}
	}

	public void setFooter( String name, String value )
	{
		footerNames.add( name );
		footerValues.add( value );
	}

	public void done() throws IOException
	{
		flush();
		PrintStream pout = new PrintStream( out );
		pout.println( "0" );
		if ( footerNames.size() > 0 )
		{
			// Send footers.
			for ( int i = 0; i < footerNames.size(); ++i )
			{
				String name = footerNames.get( i );
				String value = footerValues.get( i );
				pout.println( name + ": " + value );
			}
		}
		footerNames = null;
		footerValues = null;
		pout.println( "" );
		pout.flush();
	}

	public void close() throws IOException
	{
		if ( footerNames != null )
			done();
		super.close();
	}

	public synchronized void write( byte b[], int off, int len ) throws IOException
	{
		int avail = buf.length - count;

		if ( len <= avail )
		{
			System.arraycopy( b, off, buf, count, len );
			count += len;
			return;
		}
		flush();
		writeBuf( b, off, len );
	}

	@SuppressWarnings("deprecation")
	private void writeBuf( byte b[], int off, int len ) throws IOException
	{
		// Write the chunk length as a hex number.
		String lenStr = Integer.toString( len, 16 );
		lenStr.getBytes( 0, lenStr.length(), lenBytes, 0 );
		out.write( lenBytes );
		// Write a CRLF.
		out.write( crlf );
		// Write the data.
		if ( len != 0 )
			out.write( b, off, len );
		// Write a CRLF.
		out.write( crlf );
		// And flush the real stream.
		out.flush();
	}

}

