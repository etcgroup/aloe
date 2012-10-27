/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

/**
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public class Rating {
    private final int userId;
    private final int codeId;

    public Rating(int userId, int codeId) {
        this.userId = userId;
        this.codeId = codeId;
    }

    public int getCodeId() {
        return codeId;
    }

    public int getUserId() {
        return userId;
    }
}
