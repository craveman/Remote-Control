package server.schemas.responses;

import com.google.gson.annotations.SerializedName;

public class Training {
    
    @SerializedName("date")
    public String date;
    
    @SerializedName("address")
    public String address;
    
    @SerializedName("fightsCount")
    public Integer fightsCount;

    @Override
    public int hashCode() {
        int base = 42;
        int result = 1;
        result = base + date.length()*base + date.hashCode();
        result = base*result + address.length()*base;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
        return true;
        }
        if (getClass() != obj.getClass()) {
        return false;
        }
        Training newT = (Training) obj;

        if (!date.equalsIgnoreCase(newT.date)){
            return  false;
        }
        if (!address.equalsIgnoreCase(newT.address)) {
            return  false;
        }
        return fightsCount == newT.fightsCount;
    }
}
