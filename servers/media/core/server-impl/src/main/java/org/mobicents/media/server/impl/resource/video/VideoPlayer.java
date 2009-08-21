/*
 * Mobicents, Communications Middleware
 * 
 * Copyright (c) 2008, Red Hat Middleware LLC or third-party
 * contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Middleware LLC.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 *
 * Boston, MA  02110-1301  USA
 */

package org.mobicents.media.server.impl.resource.video;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 *
 * @author kulikov
 */
public class VideoPlayer {
    
    private String fileName;
    private FileInputStream fin;
    
    public void setFile(String fileName) {
        this.fileName = fileName;
    }
    
    
    private byte[] read(DataInputStream in) throws IOException {
        byte[] buff = new byte[4];
        for (int i = 0; i < buff.length; i++) {
            buff[i] = in.readByte();
        }
        return buff;
    }
    
    private void process() throws IOException {
        DataInputStream ds = new DataInputStream(fin);
        while (ds.available() > 0) {
            int len = ds.readInt();
            String type = new String(read(ds));
            if (type.equals("ftyp")) {
                FileTypeBox box = new FileTypeBox(len, type);
                box.load(ds); 
                System.out.println(box.getMajorBrand());
                System.out.println(box.getMinorVersion());
                System.out.println("compatible: " + box.getCompatibleBrands().length);
                for (int i = 0; i < box.getCompatibleBrands().length; i++) {
                    System.out.println(box.getCompatibleBrands()[i]);
                }
            } else if (type.equals("moov")){
                MovieBox movie = new MovieBox(len, type);
                movie.load(ds);
            } else {
                return;
            }
        }
    }
    
    private void open() throws FileNotFoundException {
        fin = new FileInputStream(fileName);
    }
    
    public void start() {
        
    }    
    
    public static void main(String[] args) throws Exception {        VideoPlayer vp = new VideoPlayer();
        vp.setFile("/home/abhayani/Desktop/tones/016.3gp");
        vp.open();
        vp.process();
    }
}
