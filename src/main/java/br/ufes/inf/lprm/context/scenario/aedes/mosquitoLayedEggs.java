/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.lprm.context.scenario.aedes;

import org.kie.api.definition.type.Expires;
import org.kie.api.definition.type.Role;

@Role(Role.Type.EVENT)
@Expires("20d")
/**
 *
 * @author alessandro
 */
public class mosquitoLayedEggs {
   private Mosquito layed;

public Mosquito getLayed() {
	return layed;
}

public void setLayed(Mosquito layed) {
	this.layed = layed;
}

   
}
