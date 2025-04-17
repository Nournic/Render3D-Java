package ru.ssau.tk.nour.image.other;

import lombok.Data;

@Data
public class ProjectScale {
    private final double shiftX;
    private final double shiftY;
    private final double shiftZ;

    private final double scaleX;
    private final double scaleY;
    private final double scaleZ;

    private final double shiftPointX;
    private final double shiftPointY;
    private final double shiftPointZ;

    private ProjectScale(Builder builder){
        this.shiftX = builder.shiftX;
        this.shiftY = builder.shiftY;
        this.shiftZ = builder.shiftZ;

        this.scaleX = builder.scaleX;
        this.scaleY = builder.scaleY;
        this.scaleZ = builder.scaleZ;

        this.shiftPointX = builder.shiftPointX;
        this.shiftPointY = builder.shiftPointY;
        this.shiftPointZ = builder.shiftPointZ;
    }

    public static class Builder{
        private double shiftX = 0;
        private double shiftZ = 0;
        private double shiftY = 0;

        private double scaleX = 1;
        private double scaleY = 1;
        private double scaleZ = 1;

        private double shiftPointX = 0;
        private double shiftPointY = 0;
        private double shiftPointZ = 0;

        public Builder(){}

        public Builder shiftX(double val){
            shiftX = val;
            return this;
        }
        public Builder shiftY(double val){
            shiftY = val;
            return this;
        }
        public Builder shiftZ(double val){
            shiftZ = val;
            return this;
        }
        public Builder scaleX(double val){
            scaleX = val;
            return this;
        }
        public Builder scaleY(double val){
            scaleY = val;
            return this;
        }
        public Builder scaleZ(double val){
            scaleZ = val;
            return this;
        }
        public Builder shiftPointX(double val){
            shiftPointX = val;
            return this;
        }
        public Builder shiftPointY(double val){
            shiftPointY = val;
            return this;
        }
        public Builder shiftPointZ(double val){
            shiftPointZ = val;
            return this;
        }

        public ProjectScale build(){
            return new ProjectScale(this);
        }
    }
}
