package com.zznode.opentnms.isearch.routeAlgorithm.api.enumrate;


public enum Policy {

	    LESS_JUMP(  0, "�����������" ),
	    WIDTH_BALANCE( 1, "���������" ),
	    MINIMUN_LATENCY( 2, "ʱ����С����" );
	    
	    private int policyCode;
	    private String policyDesc;

	    private Policy( int policyCode, String policyDesc ) {

	        this.policyCode = policyCode;
	        this.policyDesc = policyDesc;

	    }
	    
	    public static Policy definde(String code){
	    	if(code.equals("0")){
	    		return LESS_JUMP;
	    	}
	    	else if(code.equals("1")){
	    		return WIDTH_BALANCE;
	    	}
	    	else if(code.equals("2")){
	    		return MINIMUN_LATENCY;
	    	}
	    	else {
	    		return null ;
	    	}
	    }
	    
	    public static Policy defindeDesc(String desc){
	        if( desc.equals("min-hop")){
	            return LESS_JUMP;
	        }
	        else if(desc.equals("bandwidth-balancing")){
	            return WIDTH_BALANCE;
	        }
	        else if(desc.equals("min-latency")){
	            return MINIMUN_LATENCY;
	        }
	        else {
	            return null ;
	        }
	    }

	    

	    public int getPolicyCode() {
	        return policyCode;
	    }
	    public void setPolicyCode(int policyCode) {
	        this.policyCode = policyCode;
	    }
	    public String getPolicyDesc() {
	        return policyDesc;
	    }
	    public void setPolicyDesc(String policyDesc) {
	        this.policyDesc = policyDesc;
	    }



	    @Override
	    public String toString() {

	        return String.valueOf(this.policyCode + ":" + this.policyDesc );

	    }
	    
	    
	    

	}
