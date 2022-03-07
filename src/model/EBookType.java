/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;

/**
 *
 * @author user
 */
public enum EBookType implements Serializable{
    can_be_lent,cannot_be_lent,want_to_aquire;

    @Override
    public String toString() {
        switch (this) {
            case can_be_lent:
                return "Can be lent";
            case cannot_be_lent:
                return "Cannot be lent";
            case want_to_aquire:
                return "Want to acquire";
        }
           return "Cannot be lent";
    }
}
