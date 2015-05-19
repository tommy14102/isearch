package com.zznode.opentnms.isearch.routeAlgorithm.api.enumrate;


public enum Direction {

	    SINGLE(  0, "����" ),
	    DOUBLE( 1, "˫��" );
	    
	    private int code;
	    private String desc;

	    private Direction( int code, String desc ) {

	        this.code = code;
	        this.desc = desc;

	    }
	    
	    public static Direction definde(String code){
	    	if(code.equals("0")){
	    		return SINGLE;
	    	}
	    	else if(code.equals("1")){
	    		return DOUBLE;
	    	}
	    	else {
	    		return null ;
	    	}
	    }

		public int getCode() {
			return code;
		}

		public void setCode(int code) {
			this.code = code;
		}

		public String getDesc() {
			return desc;
		}

		public void setDesc(String desc) {
			this.desc = desc;
		}

	}
