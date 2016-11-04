/*
 *   MAGIC Tool, A microarray image and data analysis program
 *   Copyright (C) 2003  Laurie Heyer
 *
 *   This program is free software; you can redistribute it and/or
 *   modify it under the terms of the GNU General Public License
 *   as published by the Free Software Foundation; either version 2
 *   of the License, or (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program; if not, write to the Free Software
 *   Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 *   Contact Information:
 *   Laurie Heyer
 *   Dept. of Mathematics
 *   PO Box 6959
 */

package magictool.cluster;

/**
 * NodeInfo holds the name of a cluster and the distance between clustered nodes
 *
 */
public class NodeInfo {

    private String label;
    private float distance;

    /**
     * Creates a new nodeInfo with the cluster name and dissimilarity
     * @param label cluster number
     * @param distance dissimilarity
     */
    public NodeInfo (int label, float distance) {
        this.label = Integer.toString(label);
        this.distance = distance;
    }

    /**
     * Creates a new nodeInfo with the cluster name and dissimilarity
     * @param label cluster number
     * @param distance dissimilarity
     */
    public NodeInfo (int label, Float distance) {
        this.label = Integer.toString(label);
        this.distance = distance.floatValue();
    }

    /**
     * Creates a new nodeInfo with the cluster name and dissimilarity
     * @param label cluster name
     * @param distance dissimilarity
     */
    public NodeInfo (String label, float distance){
        this.label = label;
        this.distance = distance;

    }
    /**
     * returns the cluster name
     * @return cluster name
     */
    public String toString () {
        return  label;
    }

    /**
     * In this method we check if the distance is less than zero or 
     * greater than 2.
     * returns the cluster dissimilarity
     * @return cluster dissimilarity
     *
     */
    public float getDistance () {
    	if(distance<0) return 0;
    	if (distance>2) return 2;
    	return  distance;
    }
}