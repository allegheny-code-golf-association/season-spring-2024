package cfh.taxi.node;

import cfh.taxi.Node;

public enum NodeType {

    CORNER(null) {
        @Override
        public Node createNode(String name, int x, int y) {
            return new Corner(this, name, x, y);
        }
    },
    ROTATORY(null) {
        @Override
        public Node createNode(String name, int x, int y) {
            return new Rotatory(this, name, x, y);
        }
    },
    INTERSECTION(null) {
        @Override
        public Node createNode(String name, int x, int y) {
            return new Intersection(this, name, x, y);
        }
    },
    
    ADDITION_ALLEY("Addition Alley") {
        @Override
        public Node createNode(String name, int x, int y) {
            return new AdditionAlley(this, name, x, y);
        }
    },
    AUCTIONEER_SCHOOL("Auctioneer School") {
        @Override
        public Node createNode(String name, int x, int y) {
            return new AuctioneerSchool(this, name, x, y);
        }
    },
    CHARBOIL_GRILL("Charboil Grill") {
        @Override
        public Node createNode(String name, int x, int y) {
            return new CharboilGrill(this, name, x, y);
        }
    },
    CHOP_SUEY("Chop Suey") {
        @Override
        public Node createNode(String name, int x, int y) {
            return new ChopSuey(this, name, x, y);
        }
    },
    COLLATOR_EXPRESS("Collator Express") {
        @Override
        public Node createNode(String name, int x, int y) {
            return new CollatorExpress(this, name, x, y);
        }
    },
    CRIME_LAB("Crime Lab") {
        @Override
        public Node createNode(String name, int x, int y) {
            return new CrimeLab(this, name, x, y);
        }
    },
    CYCLONE("Cyclone") {
        @Override
        public Node createNode(String name, int x, int y) {
            return new Cyclone(this, name, x, y);
        }
    },
    DIVIDE_CONQUER("Divide and Conquer") {
        @Override
        public Node createNode(String name, int x, int y) {
            return new DivideConquer(this, name, x, y);
        }
    },
    EQUALS_CORNER("Equal's Corner") {
        @Override
        public Node createNode(String name, int x, int y) {
            return new EqualsCorner(this, name, x, y);
        }
    },
    FIREMOUTH_GRILL("Firemouth Grill") {
        @Override
        public Node createNode(String name, int x, int y) {
            return new FiremouthGrill(this, name, x, y);
        }
    },
    GAS_STATION("Gas Station") {
        @Override
        public Node createNode(String name, int x, int y) {
            return new GasStation(this, name, x, y);
        }
    },
    HEISENBERGS("Heisenberg's") {
        @Override
        public Node createNode(String name, int x, int y) {
            return new Heisenbergs(this, name, x, y);
        }
    },
    JOYLESS_PARK("Joyless Park") {
        @Override
        public Node createNode(String name, int x, int y) {
            return new JoylessPark(this, name, x, y);
        }
    },
    KNOTS_LANDING("Knots Landing") {
        @Override
        public Node createNode(String name, int x, int y) {
            return new KnotsLanding(this, name, x, y);
        }
    },
    KONKATS("KonKat's") {
        @Override
        public Node createNode(String name, int x, int y) {
            return new KonKats(this, name, x, y);
        }
    },
    LITTLE_LEAGUE_FIELD("Little League Field") {
        @Override
        public Node createNode(String name, int x, int y) {
            return new LittleLeagueField(this, name, x, y);
        }
    },
    MAGIC_EIGHT("Magic Eight") {
        @Override
        public Node createNode(String name, int x, int y) {
            return new MagicEight(this, name, x, y);
        }
    },
    MULTIPLICATION_STATION("Multiplication Station") {
        @Override
        public Node createNode(String name, int x, int y) {
            return new MultiplicationStation(this, name, x, y);
        }
    },
    NARROW_PATH_PARK("Narrow Path Park") {
        @Override
        public Node createNode(String name, int x, int y) {
            return new NarrowPathPark(this, name, x, y);
        }
    },
    POST_OFFICE("Post Office") {
        @Override
        public Node createNode(String name, int x, int y) {
            return new PostOffice(this, name, x, y);
        }
    },
    REST_BENCH("Rest or Bench") {
        @Override
        public Node createNode(String name, int x, int y) {
            return new RestBench(this, name, x, y);
        }
    },
    RIVERVIEW_BRIDGE("Riverview Bridge") {
        @Override
        public Node createNode(String name, int x, int y) {
            return new RiverviewBridge(this, name, x, y);
        }
    },
    ROUNDERS_PUB("Rounders Pub") {
        @Override
        public Node createNode(String name, int x, int y) {
            return new RoundersPub(this, name, x, y);
        }
    },
    STARCHILD_NUMEROLOGY("Starchild Numerology") {
        @Override
        public Node createNode(String name, int x, int y) {
            return new StarchildNumerology(this, name, x, y);
        }
    },
    SUNNY_SKIES_PARK("Sunny Skies Park") {
        @Override
        public Node createNode(String name, int x, int y) {
            return new SunnySkiesPark(this, name, x, y);
        }
    },
    TAXI_GARAGE("Taxi Garage") {
        @Override
        public Node createNode(String name, int x, int y) {
            return new TaxiGarage(this, name, x, y);
        }
    },
    THE_BABELFISHERY("The Babelfishery") {
        @Override
        public Node createNode(String name, int x, int y) {
            return new TheBabelfishery(this, name, x, y);
        }
    },
    THE_UNDERGROUND("The Underground") {
        @Override
        public Node createNode(String name, int x, int y) {
            return new TheUnderground(this, name, x, y);
        }
    },
    TOMS_TRIMS("Tom's Trims") {
        @Override
        public Node createNode(String name, int x, int y) {
            return new TomsTrims(this, name, x, y);
        }
    },
    TRUNKERS("Trunkers") {
        @Override
        public Node createNode(String name, int x, int y) {
            return new Trunkers(this, name, x, y);
        }
    },
    WHATS_THE_DIFFERENCE("What's The Difference") {
        @Override
        public Node createNode(String name, int x, int y) {
            return new WhatsTheDifference(this, name, x, y);
        }
    },
    WRITERS_DEPOT("Writer's Depot") {
        @Override
        public Node createNode(String name, int x, int y) {
            return new WritersDepot(this, name, x, y);
        }
    }
    ;
    
    private final String name;
    
    private NodeType(String name) {
        this.name = name;
    }
    
    @Override
    public String toString() {
        return name != null ? name : name();
    }
    
    public abstract Node createNode(String nodeName, int x, int y);
}
