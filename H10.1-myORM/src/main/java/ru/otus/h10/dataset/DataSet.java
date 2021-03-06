package ru.otus.h10.dataset;

public abstract class DataSet {

    private long id;

    public static final String ID_NAME = "id";

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

       @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        DataSet that = (DataSet)obj;
        return this.id == that.id;
    }
}

