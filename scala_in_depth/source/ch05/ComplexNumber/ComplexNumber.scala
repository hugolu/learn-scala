package complexmath

case class ComplexNumber(real: Double, imaginary: Double) {
    def *(other: ComplexNumber) = 
        ComplexNumber((real * other.real) + (imaginary * other.imaginary),
                      (real * other.imaginary) + (imaginary * other.real))
    def +(other: ComplexNumber) =
        ComplexNumber(real + other.real, imaginary + other.imaginary)
}
