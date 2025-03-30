package ru.ssau.tk.nour.image.other;

import ru.ssau.tk.nour.image.data.Point3D;

class Matrix3 {
    double[] values;
    Matrix3(double[] values) {
        this.values = values;
    }

    Matrix3 multiply(Matrix3 other) {
        double[] result = new double[9];
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                for (int i = 0; i < 3; i++) {
                    result[row * 3 + col] +=
                            this.values[row * 3 + i] * other.values[i * 3 + col];
                }
            }
        }
        return new Matrix3(result);
    }

    Point3D transform(Point3D in) {
        return new Point3D(
                in.getX() * values[0] + in.getY() * values[1] + in.getZ() * values[2],
                in.getX() * values[3] + in.getY() * values[4] + in.getZ() * values[5],
                in.getX() * values[6] + in.getY() * values[7] + in.getZ() * values[8]
        );
    }
}
