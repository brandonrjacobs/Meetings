package DAO;

//public class MeetingDAO implements IMeetingDAO {


//    public Meeting getMeeting(int id){
//        Connection conn = ConnectionFactory.getConnection();
//        try{
//            PreparedStatement prepStatement;
//            prepStatement = conn.prepareStatement("SELECT * FROM meeting where id = ?");
//            prepStatement.setInt(1, id);
//            ResultSet rs = prepStatement.executeQuery();
//
//            while(rs.next()){
//                String startdate = rs.getString(2);
//                String endDate = rs.getString(3);
//                String day = rs.getString(4);
//            }
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return new Meeting();
//    }
//}
