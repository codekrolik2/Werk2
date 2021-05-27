package org.werk2.core.config.prog.calls;

import java.util.Optional;

import org.werk2.config.Doc;
import org.werk2.config.calls.OutBinding;
import org.werk2.core.config.prog.ProgDocumented;

import lombok.NonNull;

public class ProgOutBinding extends ProgDocumented implements OutBinding {
    public ProgOutBinding(Optional<? extends Doc> doc, String fromOutParameter_, String toField_) {
		super(doc);
		this.fromOutParameter_ = fromOutParameter_;
		this.toField_ = toField_;
	}
    
    @NonNull protected String fromOutParameter_;
    @NonNull protected String toField_;
	
    @Override
	public String fromOutParameter() {
    	return fromOutParameter_;
	}
	
	@Override
	public String toField() {
        return toField_;
	}
}
