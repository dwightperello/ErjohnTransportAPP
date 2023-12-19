package com.example.erjohnandroid.database.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.erjohnandroid.database.Model.*
import com.example.erjohnandroid.database.Model.convertions.*
import com.example.erjohnandroid.database.Model.externalDispatch.TotalAmountAndTicketNumbersPerReverse
import com.example.erjohnandroid.database.repository.RoomRepository
import com.example.erjohnandroid.domain.model.response.Employee
import com.example.erjohnandroid.util.ResultState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoomViewModel @Inject constructor(private val repository: RoomRepository):ViewModel() {


//    private  var _ticketnumberstart:MutableLiveData<TicketCounterTable> = MutableLiveData()
//    val ticketnumberstart:LiveData<TicketCounterTable> = _ticketnumberstart

    private  var _businfo:MutableLiveData<List<BusInfoTableItem>> = MutableLiveData()
    val businfo:LiveData<List<BusInfoTableItem>> = _businfo


    private  var _hotspots:MutableLiveData<List<HotSpotsTable>> = MutableLiveData()
    val hotspots:LiveData<List<HotSpotsTable>> = _hotspots

    private var _company:MutableLiveData<List<CompaniesTable>> = MutableLiveData()
    val company:LiveData<List<CompaniesTable>> = _company

    private var _companyroles:MutableLiveData<List<CompanyRolesTable>> = MutableLiveData()
    val companyroles:LiveData<List<CompanyRolesTable>> = _companyroles

    private var _employees:MutableLiveData<List<EmployeesTable>> = MutableLiveData()
    val employee:LiveData<List<EmployeesTable>> = _employees

    private var _selectemployees:MutableLiveData<EmployeesTable>? = MutableLiveData()
    val selectemployees:LiveData<EmployeesTable>? = _selectemployees

    private var _selectCOnductor:MutableLiveData<List<EmployeesTable>>? = MutableLiveData()
    val selectCOnductor:LiveData<List<EmployeesTable>>? = _selectCOnductor

    private var _selectDriver:MutableLiveData<List<EmployeesTable>>? = MutableLiveData()
    val selectDriver:LiveData<List<EmployeesTable>>? = _selectDriver

    private var _expensestype:MutableLiveData<List<ExpensesTypeTable>> = MutableLiveData()
    val expensestype:LiveData<List<ExpensesTypeTable>> = _expensestype

    private var _inspectionreport:MutableLiveData<List<InspectionReportTable>> = MutableLiveData()
    val inspectionreport:LiveData<List<InspectionReportTable>> = _inspectionreport

    private var _allLines: MutableLiveData<List<LinesTable>> = MutableLiveData()
    val allLines: LiveData<List<LinesTable>> get() = _allLines

    private var _allMpadUnits: MutableLiveData<List<mPadUnitsTable>> = MutableLiveData()
    val allMpadUnits: LiveData<List<mPadUnitsTable>> get() = _allMpadUnits

    private var _linesegment:MutableLiveData<List<LineSegmentTable>> = MutableLiveData()
    val linesegment:LiveData<List<LineSegmentTable>> = _linesegment

    private var _mpadAssignment:MutableLiveData<List<mPadAssignmentsTable>> = MutableLiveData()
    val mpadAssignment:LiveData<List<mPadAssignmentsTable>> = _mpadAssignment

    private var _passengertype:MutableLiveData<List<PassengerTypeTable>> = MutableLiveData()
    val passengertype:LiveData<List<PassengerTypeTable>> = _passengertype

    private var _tripcost:MutableLiveData<List<TripCostTable>> = MutableLiveData()
    val tripcost:LiveData<List<TripCostTable>> = _tripcost

    private var _tripticket:MutableLiveData<List<TripTicketTable>> = MutableLiveData()
    val tripticket:LiveData<List<TripTicketTable>> = _tripticket

    private var _tripticketdetails:MutableLiveData<List<TripTicketTable>> = MutableLiveData()
    val tripticketdetails:LiveData<List<TripTicketTable>> = _tripticketdetails

    private var _tripticketjson:MutableLiveData<List<TripTicketGroupCount>> = MutableLiveData()
    val tripticketjson:LiveData<List<TripTicketGroupCount>> = _tripticketjson

    private var _remnorth:MutableLiveData<List<TripTicketTable>> = MutableLiveData()
    val remnorth:LiveData<List<TripTicketTable>> = _remnorth

    private var _remsouth:MutableLiveData<List<TripTicketTable>> = MutableLiveData()
    val remsouth:LiveData<List<TripTicketTable>> = _remsouth

    private var _tripwitholding:MutableLiveData<List<TripWitholdingTable>> = MutableLiveData()
    val tripwitholding:LiveData<List<TripWitholdingTable>> = _tripwitholding

    private var _witholdingtype:MutableLiveData<List<WitholdingTypeTable>> = MutableLiveData()
    val witholdingtype:LiveData<List<WitholdingTypeTable>> = _witholdingtype

    private var _partialremit:MutableLiveData<List<PartialRemitTable>> = MutableLiveData()
    val partialremit:LiveData<List<PartialRemitTable>> = _partialremit

    // INGRESSO
    private var _totaltripamount:MutableLiveData<TicketTotal> = MutableLiveData()
    val totaltripamount:LiveData<TicketTotal> = _totaltripamount

    private var _ticketsycnh:MutableLiveData<List<Sycn_TripticketTable>> = MutableLiveData()
    val ticketsycnh:LiveData<List<Sycn_TripticketTable>> = _ticketsycnh

    private var _tripreversesycnh:MutableLiveData<List<Synch_TripReverseTable>> = MutableLiveData()
    val tripreversesycnh:LiveData<List<Synch_TripReverseTable>> = _tripreversesycnh

    private var _synch_inspectionreport:MutableLiveData<List<Sycnh_InspectionReportTable>> = MutableLiveData()
    val synch_inspectionreport:LiveData<List<Sycnh_InspectionReportTable>> = _synch_inspectionreport

    private var _synch_mpad:MutableLiveData<List<Synch_mpadAssignmentsTable>> = MutableLiveData()
    val synch_mpad:LiveData<List<Synch_mpadAssignmentsTable>> = _synch_mpad

    private var _synch_partial:MutableLiveData<List<Synch_partialremitTable>> = MutableLiveData()
    val synch_partial:LiveData<List<Synch_partialremitTable>> = _synch_partial

    private var _synch_trip_cost:MutableLiveData<List<Synch_TripCostTable>> = MutableLiveData()
    val synch_trip_cost:LiveData<List<Synch_TripCostTable>> = _synch_trip_cost

    private var _synch_trip_witholdingt:MutableLiveData<List<Synch_TripwitholdingTable>> = MutableLiveData()
    val synch_trip_witholdingt:LiveData<List<Synch_TripwitholdingTable>> = _synch_trip_witholdingt

    private var _ingresso:MutableLiveData<List<IngressoTable>> = MutableLiveData()
    val ingresso:LiveData<List<IngressoTable>> = _ingresso

    private var _totalTripcost:MutableLiveData<TripCostTotal> = MutableLiveData()
    val totalTripcost:LiveData<TripCostTotal> = _totalTripcost

    private var _totalwithodling:MutableLiveData<WitholdingTotal> = MutableLiveData()
    val totalwithodling:LiveData<WitholdingTotal> = _totalwithodling

    private var _tripamountperreverse:MutableLiveData<TripAmountPerReverse> = MutableLiveData()
    val tripamountperreverse:LiveData<TripAmountPerReverse> = _tripamountperreverse


    private var _tripticketafterinspection:MutableLiveData<List<TripTicketTable>> = MutableLiveData()
    val tripticketafterinspection:LiveData<List<TripTicketTable>> = _tripticketafterinspection

    private var _ingressoRefids:MutableLiveData<List<Int>> = MutableLiveData()
    val ingressoRefids:LiveData<List<Int>> = _ingressoRefids

    private var _alltripticketforreverse:MutableLiveData<List<TotalAmountAndTicketNumbersPerReverse>> = MutableLiveData()
    val alltripticketforreverse:LiveData<List<TotalAmountAndTicketNumbersPerReverse>> = _alltripticketforreverse

    private var _tripgross:MutableLiveData<TripGross> = MutableLiveData()
    val tripgross:LiveData<TripGross> = _tripgross

    private var _terminals:MutableLiveData<List<TerminalTable>> = MutableLiveData()
    val terminals:LiveData<List<TerminalTable>> = _terminals

    private var _AllTripReverse:MutableLiveData<List<TripReverseTable>> = MutableLiveData()
    val AllTripReverse:LiveData<List<TripReverseTable>> = _AllTripReverse

    private var _Alllogreports:MutableLiveData<List<LogReport>> = MutableLiveData()
    val Alllogreports:LiveData<List<LogReport>> = _Alllogreports

    private var _synch_log_report:MutableLiveData<List<Synch_LogReport>> = MutableLiveData()
    val synch_log_report:LiveData<List<Synch_LogReport>> = _synch_log_report

    private var _fare:MutableLiveData<FareTable> = MutableLiveData()
    val fare:LiveData<FareTable> = _fare

    private var _readysycnh:MutableLiveData<Int> = MutableLiveData()
    val readysycnh:LiveData<Int> = _readysycnh

    private var _patialremitsum:MutableLiveData<Double> = MutableLiveData()
    val patialremitsum:LiveData<Double> = _patialremitsum

    //region OTHER METHODS

    init {
      getFare()
    }

    fun getBusinfo(id:Int){
        viewModelScope.launch() {
            val records=  repository.getAllBusinfo(id)
            _businfo.value=records
        }
    }

    fun getTotalPartialremit(refid: Int){
        viewModelScope.launch() {
            val records=  repository.gettotalpartialremit(refid)
            _patialremitsum.value=records
        }
    }

    fun getreadysynch(){
        viewModelScope.launch() {
            val records=  repository.getreadyforsynch()
            _readysycnh.value=records
        }
    }

    fun gethotspots(id:Int){
        viewModelScope.launch() {
            val records=  repository.getHotspot(id)
            _hotspots.value=records
        }
    }

    fun getFare(){
        viewModelScope.launch() {
            val records=  repository.getFares()
            _fare.value=records
        }
    }

    fun insertBusinfo(entity: List<BusInfoTableItem>){
        viewModelScope.launch() {
            val records=  repository.insertBusinfoBulk(entity)

        }
    }

    fun insertLogreportbulk(entity: List<LogReport>){
        viewModelScope.launch() {
            val records=  repository.insertLogreportbulk(entity)

        }
    }

    fun insertLogReport(entity: LogReport){
        viewModelScope.launch() {
            val records=  repository.insertLogReport(entity)

        }
    }

    fun insertTripReverse(entity: List<TripReverseTable>){
        viewModelScope.launch() {
            val records=  repository.insertTripTeverseBulk(entity)

        }
    }

    fun insertMpadUnits(entity: List<mPadUnitsTable>){
        viewModelScope.launch() {
            val records=  repository.insertMpadunitsBulk(entity)

        }
    }

  suspend  fun insertfare(entity: FareTable){
        viewModelScope.launch() {
            val records=  repository.insertFare(entity)
        }
    }

    fun getMpadUnits(){
        viewModelScope.launch() {
            val records=  repository.getMpadUnits()
            _allMpadUnits.value=records
        }
    }

    fun getAllTripReverse(){
        viewModelScope.launch() {
            val records=  repository.getTripReverseAll()
            _AllTripReverse.value=records
        }
    }

//    fun inserticketnu(entity: TicketCounterTable){
//        viewModelScope.launch() {
//            val records=  repository.insertticketnum(entity)
//
//        }
//    }

    fun insertAllHotspots(entity: List<HotSpotsTable>){
        viewModelScope.launch() {
            val records=  repository.insertHotspotBulk(entity)

        }
    }

    fun getCompanies(){
        viewModelScope.launch() {
            val records=  repository.getAllCompanies()
            _company.value=records
        }
    }

    fun insertCompany(entity: List<CompaniesTable>){
        viewModelScope.launch() {
            val records=  repository.insertComapniesBulk(entity)

        }
    }

    fun getCompanyRoles(){
        viewModelScope.launch() {
            val records=  repository.getAllCompanyRoles()
            _companyroles.value=records
        }
    }

    fun insertCompanyRoles(entity: List<CompanyRolesTable>){
        viewModelScope.launch() {
            val records=  repository.insertCompanyrolesBulk(entity)

        }
    }

    fun getEmployee(id:Int){
        viewModelScope.launch() {
            val records=  repository.getEmployees(id)
            _employees.value=records
        }
    }

    fun selectEmployee(pin:Int){
        viewModelScope.launch() {
            val records=  repository.selectEmployee(pin)
            _selectemployees?.value=records
        }
    }

    fun selectConductor(roleid:Int){
        viewModelScope.launch() {
            val records=  repository.selectConductor(roleid)
            _selectCOnductor?.value=records
        }
    }

    fun selectDriver(roleid:Int){
        viewModelScope.launch() {
            val records=  repository.selectDriver(roleid)
            _selectDriver?.value=records
        }
    }

    fun insertEmployeeBulk(entity: List<EmployeesTable>){
        viewModelScope.launch() {
            val records=  repository.insertEmployeesBulk(entity)

        }
    }

    fun getExpensesType(){
        viewModelScope.launch() {
            val records=  repository.getExpensestype()
            _expensestype.value=records
        }
    }

    fun getTotalTripcost(){
        viewModelScope.launch() {
            val records=  repository.getTotalTripcost()
            _totalTripcost.value=records
        }
    }

    fun getTotalwithlding(){
        viewModelScope.launch() {
            val records=  repository.getTotalWItholding()
            _totalwithodling.value=records
        }
    }

    fun insertExpensestypeBulk(entity: List<ExpensesTypeTable>){
        viewModelScope.launch() {
            val records=  repository.insertExpensesTypeBulk(entity)

        }
    }

    fun getInspectionReport(){
        viewModelScope.launch() {
            val records=  repository.getInspectionReport()
            _inspectionreport.value=records
        }
    }

    fun insertInspectionReportBulk(entity: InspectionReportTable){
        viewModelScope.launch() {
            val records=  repository.insertInspectionReportBulk(entity)

        }
    }

    fun getAllLines(){
        viewModelScope.launch() {
          val records=  repository.getAllLines()
            _allLines.value=records
        }
    }

    fun getAllTerminal(){
        viewModelScope.launch() {
            val records=  repository.getTerminals()
            _terminals.value=records
        }
    }


    fun insertAllLines(entity:List<LinesTable>){
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertLines(entity)
        }
    }

    fun getLinesegment(id:Int){
        viewModelScope.launch() {
            val records=  repository.getlinsegment(id)
            _linesegment.value=records
        }
    }

    fun insertLinesegmentBulk(entity: List<LineSegmentTable>){
        viewModelScope.launch() {
            val records=  repository.insertLinesegmentBulk(entity)

        }
    }

    fun getMpadAssignment(){
        viewModelScope.launch() {
            val records=  repository.getmpadAssignments()
            _mpadAssignment.value=records
        }
    }

    fun insertmPadAssignmentBulk(entity: List<mPadAssignmentsTable>){
        viewModelScope.launch() {
            val records=  repository.insertMpadAssignmentbulk(entity)

        }
    }

    fun getPassengerType(){
        viewModelScope.launch() {
            val records=  repository.getPassengertype()
            _passengertype.value=records
        }
    }

    fun insertPassengerTypeBUlk(entity: List<PassengerTypeTable>){
        viewModelScope.launch() {
            val records=  repository.insertPassengerTYpeBUlk(entity)

        }
    }

    fun getTripcost(){
        viewModelScope.launch() {
            val records=  repository.getTripcost()
            _tripcost.value=records
        }
    }

    fun inserTirpcostBUlk(entity: TripCostTable){
        viewModelScope.launch() {
            val records=  repository.inserttripcostBUlk(entity)

        }
    }

    fun getTripticket(){
        viewModelScope.launch() {
            val records=  repository.getTripTicket()
            _tripticket.value=records
        }
    }

    fun getTripticketdetails(reverse: Int){
        viewModelScope.launch() {
            val records=  repository.getTicketdetails(reverse)
            _tripticketdetails.value=records
        }
    }

    fun getReverse(){
        viewModelScope.launch() {
            val records=  repository.getTripReverse()
            _tripticketjson.value=records
        }
    }

    fun getRemNorth(kmorigin:Int,reverse:Int){
        viewModelScope.launch() {
            val records=  repository.getRemNorth(kmorigin,reverse)
            _remnorth.value=records
        }
    }

    fun getRemSouth(kmorigin:Int,reverse:Int){
        viewModelScope.launch() {
            val records=  repository.getRemSouth(kmorigin,reverse)
            _remsouth.value=records
        }
    }

    fun insertTripTicketBulk(entity: TripTicketTable){
        viewModelScope.launch() {
            val records=  repository.insertTripticketBulk(entity)
        }
    }

    fun getTripwitholding(){
        viewModelScope.launch() {
            val records=  repository.getTripWitholding()
            _tripwitholding.value=records
        }
    }

    fun insertTripwitholdingbulk(entity: TripWitholdingTable){
        viewModelScope.launch() {
            val records=  repository.insertTripWitholdingbulk(entity)

        }
    }

    fun updateripwitholding(refid: Int, amount: Double, witholdingtype: String){
        viewModelScope.launch() {
            val records=  repository.updateWitholding(refid,amount,witholdingtype)

        }
    }

    fun updaterExprenses(refid: Int, amount: Double, costype: String){
        viewModelScope.launch() {
            val records=  repository.updateExpenses(refid,amount,costype)

        }
    }

   fun getwitholdingtype(){
       viewModelScope.launch() {
           val records=  repository.getWitholdingtype()
           _witholdingtype.value=records
       }
    }

    fun insertWitholdingtypebulk(entity: List<WitholdingTypeTable>){
        viewModelScope.launch() {
            val records=  repository.insertWitholdingtype(entity)

        }
    }

    fun getPartialRemit(){
        viewModelScope.launch() {
            val records=  repository.getPartialRemit()
            _partialremit.value=records
        }
    }

    fun insertPartialremit(entity: PartialRemitTable){
        viewModelScope.launch() {
            val records=  repository.insertPartialRemit(entity)

        }
    }

    fun getTripTicketafterInspection(searchkm:Int,reverse: Int){
        viewModelScope.launch() {
            val records=  repository.getTripticketafterInspection(searchkm,reverse)
            _tripticketafterinspection.value=records
        }
    }

    fun getTripTicketafterInspectionNorth(searchkm:Int,reverse: Int){
        viewModelScope.launch() {
            val records=  repository.getTripticketafterInspectionNorth(searchkm,reverse)
            _tripticketafterinspection.value=records
        }
    }

    //endregion

    //region INGRESSO

    fun getAllIngressoRefID(){
        viewModelScope.launch() {
            val records = repository.getAllIngressoRefID()
            _ingressoRefids.value = records
        }
    }
    fun getTotalAmountTrip() {
        viewModelScope.launch() {
            val records = repository.getTotalAmount()
            _totaltripamount.value = records
        }
    }

    fun insertIngersso(entity: IngressoTable){
        viewModelScope.launch() {
            val records=  repository.insertIngresso(entity)

        }
    }

    fun getAllIngresso(refid:Int){
        viewModelScope.launch() {
            val records = repository.getAllIngresso(refid)
            _ingresso.value = records
        }
    }

    fun getTicketsForSynch(refid: Int){
        viewModelScope.launch() {
            val records = repository.getallsynchTripticket(refid)
            _ticketsycnh.value = records
        }
    }

    fun getTripReverseForSynch(refid: Int){
        viewModelScope.launch() {
            val records = repository.getallsynchTripReverse(refid)
            _tripreversesycnh.value = records
        }
    }

    fun insertticketsynch(entity:List<Sycn_TripticketTable>){
        viewModelScope.launch() {
            val records=  repository.insertTripTicketsynch(entity)

        }
    }

    fun get_synch_inspection(refid:Int){
        viewModelScope.launch() {
            val records = repository.get_synch_inspection(refid)
            _synch_inspectionreport.value = records
        }
    }

    fun insert_synch_inspection(entity: List<Sycnh_InspectionReportTable>){
        viewModelScope.launch() {
            val records=  repository.insert_synch_inspection(entity)

        }
    }

    fun insert_synch_TripReverse(entity: List<Synch_TripReverseTable>){
        viewModelScope.launch() {
            val records=  repository.insert_synch_TripReverse(entity)

        }
    }

    fun get_synch_mpad(refid: Int){
        viewModelScope.launch() {
            val records = repository.get_synch_mpad(refid)
            _synch_mpad.value = records
        }
    }

    fun get_synch_logReport(refid: Int){
        viewModelScope.launch() {
            val records = repository.get_synch_logreport(refid)
            _synch_log_report.value = records
        }
    }

    fun get_logReport(){
        viewModelScope.launch() {
            val records = repository.getLogReports()
            _Alllogreports.value = records
        }
    }

    fun insert_synch_mpad(entity: List<Synch_mpadAssignmentsTable>){
        viewModelScope.launch() {
            val records=  repository.insert_synch_mpad(entity)

        }
    }

    fun get_synch_partial_remit(refid: Int){
        viewModelScope.launch() {
            val records = repository.get_synch_partial_remit(refid)
            _synch_partial.value = records
        }
    }

    fun insert_synch_partial_remit(entity: List<Synch_partialremitTable>){
        viewModelScope.launch() {
            val records=  repository.insert_synch_partial_remit(entity)

        }
    }

    fun get_synch_trip_cost(refid: Int){
        viewModelScope.launch() {
            val records = repository.get_synch_trip_cost(refid)
            _synch_trip_cost.value = records
        }
    }

    fun insert_synch_trip_cost(entity: List<Synch_TripCostTable>){
        viewModelScope.launch() {
            val records=  repository.insert_synch_trip_cost(entity)

        }
    }

    fun get_synch_trip_witholding(refid: Int){
        viewModelScope.launch() {
            val records = repository.get_synch_witholding(refid)
            _synch_trip_witholdingt.value = records
        }
    }

    fun getTripAmountPerReverse(reverse: Int){
        viewModelScope.launch() {
            val records = repository.getTripAmountPerReverse(reverse)
            _tripamountperreverse.value = records
        }
    }

    fun insert_synch_witholding(entity: List<Synch_TripwitholdingTable>){
        viewModelScope.launch() {
            val records=  repository.insert_synch_witholding(entity)

        }
    }

    fun insert_synch_LogReport(entity: List<Synch_LogReport>){
        viewModelScope.launch() {
            val records=  repository.insert_synch_LogReport(entity)

        }
    }

    fun truncatetables(){
        viewModelScope.launch() {
            try {
                val records=  repository.truncatetables()
            }catch (e:java.lang.Exception){
                Log.e("erwithold",e.localizedMessage)
            }


        }
    }

    fun truncateCopyTables(){
        viewModelScope.launch() {
            val records=  repository.truncateCopyTables()

        }
    }

    fun truncateBeforeUpdate(){
        viewModelScope.launch() {
            val records=  repository.truncateForUpdate()

        }
    }

    fun insertTripticketBulkTwo(entity: List<TripTicketTable>){
        viewModelScope.launch() {
            val records=  repository.insertTripticketBulkTwo(entity)

        }
    }

    fun getAllTripTicketForReverse(){
        viewModelScope.launch() {
            val records=  repository.getAllTripTicketForReverse()
            _alltripticketforreverse.value=records
        }
    }

    fun getGross(){
        viewModelScope.launch() {
            val records=  repository.getGross()
            _tripgross.value=records
        }
    }

    fun insertTerminalsBulk(entity: List<TerminalTable>){
        viewModelScope.launch() {
            val records=  repository.insertTerminalsBulk(entity)

        }
    }
    //endregion
}