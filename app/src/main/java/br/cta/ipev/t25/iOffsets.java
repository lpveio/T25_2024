package br.cta.ipev.t25;

public interface iOffsets {
    public void set_offset(Index[] indices, boolean typeZero, double[] loadCVT);

    public void set_no_offset(Index[] indices);

    public void setDETOTT(int detot);
}
