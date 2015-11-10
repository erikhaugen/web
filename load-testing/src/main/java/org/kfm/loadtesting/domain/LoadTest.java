/**
 * Created: Nov 9, 2015 5:56:55 PM
 *      by: kmurdoff
 */
package org.kfm.loadtesting.domain;

import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kevin.murdoff@neustar.biz">Kevin F. Murdoff</a>
 */
@Entity
public class LoadTest {

	@Id
    @GeneratedValue
    @Column(name = "load_test_id")
    private long id;

    @Temporal(value = TemporalType.TIMESTAMP)
    @NotNull
    @Column(name = "start")
    private Date start;

    @Temporal(value = TemporalType.TIMESTAMP)
    @NotNull
    @Column(name = "end")
    private Date end;

    @Basic(optional = false)
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "region")
    private LoadTestRegion region;
    
    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_load_test_id")
    private LoadTest parentLoadTest;
    
    @OneToMany(mappedBy = "parentLoadTest", cascade = CascadeType.ALL)
    private List<LoadTest> childrenLoadTests;

	/**
	 * <p></p>
	 * 
	 */
	public LoadTest() {
		
	}

}
