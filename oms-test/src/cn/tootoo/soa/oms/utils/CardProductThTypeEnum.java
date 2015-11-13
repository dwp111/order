package cn.tootoo.soa.oms.utils;


public enum CardProductThTypeEnum {
	ONECE("0","单次提领"),
	MANY_EQ("1","多次等额提领"),
	MANY_NE("2","多次不等额提领");
	
	private String c;
	private String s;
	
	CardProductThTypeEnum(String c, String s){
		this.c = c;
		this.s = s;
	}

	public String getC() {
		return c;
	}

	public String getS() {
		return s;
	}
	
	public static String getSByC(String c) {
		CardProductThTypeEnum cardProductThTypeEnum = CardProductThTypeEnum.get(c);
		return null == cardProductThTypeEnum ? "" : cardProductThTypeEnum.s;
	}

	public static CardProductThTypeEnum get(String c) {
		CardProductThTypeEnum cardProductThTypeEnum = null;

		for (CardProductThTypeEnum o : CardProductThTypeEnum.values()) {
			if (c.equals(o.c)) {
				cardProductThTypeEnum = o;
				break;
			}
		}

		return cardProductThTypeEnum;
	}


    /**
     * 是否单次提领
     * @return
     */
    public static boolean isOnece(String c) {
        if(c.equals(CardProductThTypeEnum.ONECE.getC()))
            return true;
        return false;
    }

}
