package com.k_nakamura.horiojapan.webupdatechecker;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2016/08/31.
 */
public class CalcStringDifference {
    static ResultStringArrays getStringLinesDifference(String[] strArray1, String[] strArray2, CheckListData clData)
    {
        int num_cols = strArray1.length + 1;
        int num_rows = strArray2.length + 1;
        Node[][] nodes = new Node[num_rows][num_cols];
        for(int r = 0; r < num_rows ; r++)
        {
            for(int c = 0; c < num_cols ; c++)
            {
                nodes[r][c] = new Node();
            }
        }

        // Initialize the leftmost column.
        for (int r = 0; r < num_rows; r++)
        {
            nodes[r][0].distance = r;
            nodes[r][0].direction = Directions.FROM_ABOVE;
        }

        // Initialize the top row.
        for (int c = 0; c < num_cols; c++)
        {
            nodes[0][c].distance = c;
            nodes[0][c].direction = Directions.FROM_LEFT;
        }

        for(int c = 1 ; c < num_cols ; c++)
        {
            for(int r = 1 ; r < num_rows ; r++)
            {
                int distance1 = nodes[r-1][c].distance + 1;
                int distance2 = nodes[r][c-1].distance + 1;
                int distance3 = Integer.MAX_VALUE;

                if(strArray1[c-1].equals(strArray2[r-1]))
                {
                    distance3 = nodes[r-1][c-1].distance;
                }

                if((distance1 <= distance2) && (distance1 <= distance3))
                {
                    nodes[r][c].distance = distance1;
                    nodes[r][c].direction = Directions.FROM_ABOVE;
                }
                else if(distance2 <= distance3)
                {
                    nodes[r][c].distance = distance2;
                    nodes[r][c].direction = Directions.FROM_LEFT;
                }
                else
                {
                    nodes[r][c].distance = distance3;
                    nodes[r][c].direction = Directions.FROM_DIAGONAL;
                }
            }
        }

        int r = num_rows - 2;
        int c = num_cols - 2;
        List<String> addList = new ArrayList<>();
        List<String> deleteList = new ArrayList<>();
        while(r > 0 || c > 0)
        {
            switch(nodes[r][c].direction)
            {
                case FROM_ABOVE:
                    if(!GetHtmlTask.isIgnoreStr(strArray2[r], clData))
                        addList.add(0,strArray2[r]);
                    r--;
                    break;
                case FROM_LEFT:
                    if(!GetHtmlTask.isIgnoreStr(strArray1[c], clData))  deleteList.add(0, strArray1[c]);
                    c--;
                    break;
                case FROM_DIAGONAL:
                    r--;
                    c--;
                    break;
            }
        }

        return new ResultStringArrays(addList, deleteList);
    }

    private static class Node
    {
        public int distance;
        public Directions direction;

        public Node()
        {
            distance = 0;
            direction = Directions.None;
        }

    }
    private enum Directions
    {
        FROM_ABOVE,
        FROM_LEFT,
        FROM_DIAGONAL,
        None
    }

    static public class ResultStringArrays
    {
        private List<String> addStringArray = new ArrayList<>();
        private List<String> deleteStringArray = new ArrayList<>();

        public ResultStringArrays(List<String> addStringArray, List<String> deleteStringArray)
        {
            this.addStringArray = addStringArray;
            this.deleteStringArray = deleteStringArray;
        }

        public List<String> getDeleteStringArray() {
            return deleteStringArray;
        }

        public List<String> getAddStringArray() {
            return addStringArray;
        }
    }
}
