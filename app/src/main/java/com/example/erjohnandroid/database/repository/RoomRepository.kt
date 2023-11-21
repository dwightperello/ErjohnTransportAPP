package com.example.erjohnandroid.database.repository

import com.example.erjohnandroid.database.Model.*
import com.example.erjohnandroid.database.Model.convertions.*
import com.example.erjohnandroid.database.Model.externalDispatch.TotalAmountAndTicketNumbersPerReverse
import com.example.erjohnandroid.database.dao.*
import com.example.erjohnandroid.util.GlobalVariable
import javax.inject.Inject

class RoomRepository @Inject constructor(private val lineDao: LineDao,private val lineSegmentDao: LineSegmentDao,private val busInfoDao: BusInfoDao,private val companiesDao: CompaniesDao,
                                         private val companyRoleDao: CompanyRoleDao,private val employeesDao: EmployeesDao,private val expensesTypeDao: ExpensesTypeDao,
                                         private val inspectionReportDao: InspectionReportDao,private val mPadAssignmentsDao: mPadAssignmentsDao,
                                         private val passengerTypeDao: PassengerTypeDao,private val tripCostDao: TripCostDao,
                                         private val tripTicketDao: TripTicketDao,private val tripWitholdingDao: TripWitholdingDao,private val witholdingTypeDao: WitholdingTypeDao,
                                         private val partialRemitDao: PartialRemitDao, private val ingressoDao: IngressoDao,private val synchTripticketdao: Synch_TripticketDao,
                                         private val synchInspectionreportdao: Synch_InspectionReportDao, private val synchMpadassignmentdao: Synch_mPadAssignmentDao, private val ticketnumberdao: TicketNumDAO,
                                         private val synchPartialremitdao: Synch_PartialRemitDao,private val synchTripcostdao: Synch_TripcostDao,private val synchTripwitholdingdao: Synch_TripwitholdingDao,private val hotSpotDAO: HotSpotDAO,private val mPadUnitsDao: mPadUnitsDao) {

    //region

    fun getTicketnumbers():TicketCounterTable{
        return ticketnumberdao.getTicketstart()
    }

    fun updateTicketnumbers(ticketcounter:Int,refid: Int, id: Int){
        return ticketnumberdao.updateTicketstart(ticketcounter,refid, id)
    }
    fun insertticketnum(entity:TicketCounterTable){
        return ticketnumberdao.insertticketnum(entity)
    }

    fun getAllBusinfo(id:Int):List<BusInfoTableItem>{
        return busInfoDao.getBusinfo(id)
    }

    fun getHotspot(id: Int):List<HotSpotsTable>{
        return  hotSpotDAO.getHotspots(id)
    }

    fun insertBusinfoBulk(entity:List<BusInfoTableItem>){
        return busInfoDao.insertBusinfoBulk(entity)
    }

    fun insertMpadunitsBulk(entity:List<mPadUnitsTable>){
        return mPadUnitsDao.insertMpadUnitsBUlk(entity)
    }

    fun getMpadUnits():List<mPadUnitsTable>{
        return mPadUnitsDao.getMpadUnits()
    }

    fun insertHotspotBulk(entity:List<HotSpotsTable>){
        return hotSpotDAO.insertAllHotSpots(entity)
    }

    fun getAllCompanies():List<CompaniesTable>{
        return  companiesDao.getCompanies()
    }

    fun insertComapniesBulk(entity: List<CompaniesTable>){
        return companiesDao.insertCompaniesBulk(entity)
    }

    fun getAllCompanyRoles():List<CompanyRolesTable>{
        return companyRoleDao.getAllCompanyroles()
    }

    fun insertCompanyrolesBulk(entity: List<CompanyRolesTable>){
        return companyRoleDao.insertCompanyrrolBulk(entity)
    }

    fun getEmployees(id:Int):List<EmployeesTable>{
        return employeesDao.getEmployees(id)
    }

    fun selectEmployee(pin:Int): EmployeesTable{
        return  employeesDao.selectEmployee(pin)
    }

    fun selectConductor(companyroleid:Int):List<EmployeesTable> {
        return  employeesDao.selectConductor(companyroleid)
    }

    fun selectDriver(companyroleid: Int):List<EmployeesTable>{
        return  employeesDao.selectDriver(companyroleid)
    }


    fun insertEmployeesBulk(entity: List<EmployeesTable>){
        return employeesDao.insertEmployeeBulk(entity)
    }

    fun getExpensestype():List<ExpensesTypeTable>{
        return expensesTypeDao.getExpensesStype()
    }

    fun insertExpensesTypeBulk(entity: List<ExpensesTypeTable>){
        return expensesTypeDao.insertExpensesTypeBulk(entity)
    }

    fun getInspectionReport():List<InspectionReportTable>{
        return inspectionReportDao.getInspectionReport(GlobalVariable.ingressoRefId)
    }

    fun insertInspectionReportBulk(entity: InspectionReportTable){
        return inspectionReportDao.insertInspectionReportBulk(entity)
    }

    fun getAllLines():List<LinesTable>{
        return lineDao.getAllLines()
    }

    fun insertLines(entity:List<LinesTable>){
        return lineDao.insertAllLines(entity)
    }

    fun getlinsegment(id:Int):List<LineSegmentTable>{
        return lineSegmentDao.getAllLinesegment(id)
    }

    fun insertLinesegmentBulk(entity: List<LineSegmentTable>){
        return lineSegmentDao.insertAllLinesegment(entity)
    }

    fun getmpadAssignments():List<mPadAssignmentsTable>{
        return mPadAssignmentsDao.getmpadAssignment(GlobalVariable.ingressoRefId)
    }

    fun insertMpadAssignmentbulk(entity: List<mPadAssignmentsTable>){
        return mPadAssignmentsDao.insertmpadassignmentBulk(entity)
    }

    fun getPassengertype():List<PassengerTypeTable>{
        return passengerTypeDao.getPassengerTYpe()
    }

    fun insertPassengerTYpeBUlk(entity: List<PassengerTypeTable>){
        return passengerTypeDao.insertPassengerTypeBulk(entity)
    }

    fun getTripcost():List<TripCostTable>{
        return tripCostDao.getTripCost(GlobalVariable.ingressoRefId)
    }

    fun inserttripcostBUlk(entity: List<TripCostTable>){
        return tripCostDao.inserTripcostBulk(entity)
    }

    fun getTripTicket():List<TripTicketTable>{
        return  tripTicketDao.getTriptikcet(GlobalVariable.ingressoRefId)
    }

    fun getTicketdetails(reverse:Int):List<TripTicketTable>{
        return  tripTicketDao.getticketdetails(reverse)
    }

    fun insertTripticketBulk(entity: TripTicketTable){
        return tripTicketDao.inserTripticketBulk(entity)
    }

    fun getTripReverse():List<TripTicketGroupCount>{
        return  tripTicketDao.getReverse()
    }

    fun getTripWitholding():List<TripWitholdingTable>{
        return tripWitholdingDao.gettripwitholding(GlobalVariable.ingressoRefId)
    }

    fun insertTripWitholdingbulk(entity: List<TripWitholdingTable>){
        return tripWitholdingDao.insertripwitholdingBulk(entity)
    }

    fun getWitholdingtype():List<WitholdingTypeTable>{
        return witholdingTypeDao.getWitholdingtype()
    }

    fun insertWitholdingtype(entity: List<WitholdingTypeTable>){
        return witholdingTypeDao.insertwitholdingtypeBUlk(entity)
    }

    fun insertPartialRemit(entity:PartialRemitTable){
        return partialRemitDao.insertPartialremit(entity)
    }

    fun getPartialRemit():List<PartialRemitTable>{
        return partialRemitDao.getPartialRemit(GlobalVariable.ingressoRefId)
    }


    //REMAINING PASSENGER
    fun getRemNorth(kmorigin:Int,reverse:Int):List<TripTicketTable>{
        return tripTicketDao.getRemNort(kmorigin,reverse)
    }

    fun getRemSouth(kmorigin:Int,reverse:Int):List<TripTicketTable>{
        return tripTicketDao.getRemSouth(kmorigin,reverse)
    }

    //endregion

    //INGRESSO
    fun getTotalAmount() : TicketTotal{
        return tripTicketDao.getTotalAmount()
    }

    fun insertIngresso(entity: IngressoTable){
        return ingressoDao.inserIngresso(entity)
    }

    fun getAllIngresso(refid:Int):List<IngressoTable>{
        return  ingressoDao.getAllIngresso(refid)
    }

    fun insertTripTicketsynch(entity:List<Sycn_TripticketTable>){
        return synchTripticketdao.insertTripTicket(entity)
    }

    fun getallsynchTripticket(refid: Int):List<Sycn_TripticketTable>{
        return  synchTripticketdao.getAllsynctickets(refid)
    }

    fun insert_synch_inspection(entity:List<Sycnh_InspectionReportTable>){
        return synchInspectionreportdao.insertsynch_inspection(entity)
    }

    fun get_synch_inspection(refid: Int):List<Sycnh_InspectionReportTable>{
        return  synchInspectionreportdao.getsynch_inspection(refid)
    }

    fun insert_synch_mpad(entity:List<Synch_mpadAssignmentsTable>){
        return synchMpadassignmentdao.insert_synch_mpad(entity)
    }

    fun get_synch_mpad(refid: Int):List<Synch_mpadAssignmentsTable>{
        return  synchMpadassignmentdao.get_synch_mpad(refid)
    }

    fun get_synch_partial_remit(refid: Int):List<Synch_partialremitTable>{
        return  synchPartialremitdao.get_synch_partial_remit(refid)
    }

    fun insert_synch_partial_remit(entity:List<Synch_partialremitTable>){
        return synchPartialremitdao.insert_synch_partial_remit(entity)
    }

    fun get_synch_trip_cost(refid: Int):List<Synch_TripCostTable>{
        return  synchTripcostdao.get_synch_trip_cost(refid)
    }

    fun insert_synch_trip_cost(entity:List<Synch_TripCostTable>){
        return synchTripcostdao.insert_synch_trip_cost(entity)
    }

    fun get_synch_witholding(refid: Int):List<Synch_TripwitholdingTable>{
        return  synchTripwitholdingdao.get_synch_witholding(refid)
    }

    fun insert_synch_witholding(entity:List<Synch_TripwitholdingTable>){
        return synchTripwitholdingdao.insert_synch_witholding(entity)
    }

    fun getAllIngressoRefID():List<Int>{
        return ingressoDao.getAllIngressoreif()
    }

    fun truncatetables(){
        inspectionReportDao.truncatinspection()
        mPadAssignmentsDao.truncatempad()
        partialRemitDao.truncatepartial()
        tripCostDao.truncateTripcost()
        tripTicketDao.truncateTripticket()
        tripWitholdingDao.truncatetripWitholding()
    }

    fun truncateCopyTables(){
        ingressoDao.truncateingresso()
        synchInspectionreportdao.truncatecopyinspection()
        synchMpadassignmentdao.truncatecopympadAssignment()
        synchPartialremitdao.truncatecopypartilaremit()
        synchTripcostdao.truncatecopytripcost()
        synchTripticketdao.truncatecopytripticket()
        synchTripwitholdingdao.truncatecopywitholdings()
    }

    fun truncateForUpdate(){
        lineDao.truncateLines()
        busInfoDao.truncateBusinfo()
        companiesDao.truncateCopaniesDao()
        companyRoleDao.truncateCompanyrole()
        employeesDao.truncateEmployees()
        expensesTypeDao.truncateExpenses()
        hotSpotDAO.truncateHotspot()
        lineSegmentDao.truncateLineSegment()
        passengerTypeDao.truncatePassengertype()
        witholdingTypeDao.truncatewitholdingType()
    }

    fun insertTripticketBulkTwo(entity:List<TripTicketTable>){
        return tripTicketDao.inserTripticketBulktwo(entity)
    }


    fun getTotalTripcost(): TripCostTotal{
        return tripCostDao.getTotalAmountTripcost()
    }

    fun getTotalWItholding():WitholdingTotal {
        return tripWitholdingDao.getTotalAmountWitholdingcost()
    }

    fun getTripAmountPerReverse(reverse:Int): TripAmountPerReverse{
        return tripTicketDao.getPerTripAmount(reverse)
    }

    fun getTripticketafterInspection(searchkm:Int, reverse:Int): List<TripTicketTable>{
        return inspectionReportDao.getTripticketsAfterInspection(searchkm, reverse)
    }

    fun getTripticketafterInspectionNorth(searchkm:Int,reverse: Int): List<TripTicketTable>{
        return inspectionReportDao.getTripticketsAfterInspectionNorth(searchkm,reverse)
    }

    fun getAllTripTicketForReverse():List<TotalAmountAndTicketNumbersPerReverse>{
        return  tripTicketDao.getAllTicketsForReverse()
    }

    fun getGross():TripGross{
        return  tripTicketDao.getGross()
    }


}