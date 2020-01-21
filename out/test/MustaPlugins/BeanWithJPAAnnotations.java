package com.mealgusto.api;

import com.mealgusto.api.abstractEntities.TemporalAuditing;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "adresses", schema = "meal_gusto_db")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class AdresseEntity extends TemporalAuditing {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Basic
    @Column(name = "city")
    private String city;

    @Basic
    @Column(name = "zip")
    private String zip;

    @Basic
    @Column(name = "country")
    private String country;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        AdresseEntity that = (AdresseEntity) o;
        if (id != that.id)
            return false;
        if (city != null ? !city.equals(that.city) : that.city != null)
            return false;
        if (zip != null ? !zip.equals(that.zip) : that.zip != null)
            return false;
        if (country != null ? !country.equals(that.country) : that.country != null)
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (city != null ? city.hashCode() : 0);
        result = 31 * result + (zip != null ? zip.hashCode() : 0);
        result = 31 * result + (country != null ? country.hashCode() : 0);
        return result;
    }
}
