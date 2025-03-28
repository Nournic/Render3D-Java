package ru.ssau.tk.nour.image.other;

import lombok.Data;
import ru.ssau.tk.nour.image.data.Polygon;

import java.util.ArrayList;

@Data
public class ModelRotate implements ImageTransform{
    private final double alpha;
    private final double beta;
    private final double gamma;

    private final double shiftX;
    private final double shiftY;
    private final double shiftZ;

    /**
     * Строитель класса <code>ImageRotate</code>, принимающий в себя углы поворота
     * модели в пространстве, а также смещение по каждой координате.
     * */
    public static class Builder{
        private double alpha  = 0;
        private double beta   = 0;
        private double gamma  = 0;

        private double shiftX = 0;
        private double shiftY = 0;
        private double shiftZ = 0;

        public Builder(){}

        public Builder alpha(double val) {
            this.alpha = val;
            return this;
        }

        public Builder beta(double val) {
            this.beta = val;
            return this;
        }

        public Builder gamma(double val) {
            this.gamma = val;
            return this;
        }

        public Builder shiftX(double val) {
            this.shiftX = val;
            return this;
        }

        public Builder shiftY(double val) {
            this.shiftY = val;
            return this;
        }

        public Builder shiftZ(double val) {
            this.shiftZ = val;
            return this;
        }

        public ModelRotate build(){
            return new ModelRotate(this);
        }
    }

    private ModelRotate(Builder builder){
        this.alpha = builder.alpha;
        this.beta = builder.beta;
        this.gamma = builder.gamma;

        this.shiftX = builder.shiftX;
        this.shiftY = builder.shiftY;
        this.shiftZ = builder.shiftZ;
    }
}
