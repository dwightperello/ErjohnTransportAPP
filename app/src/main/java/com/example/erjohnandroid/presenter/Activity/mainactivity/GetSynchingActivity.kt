package com.example.erjohnandroid.presenter.Activity.mainactivity

import android.os.Bundle
import android.os.RemoteException
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.room.PrimaryKey
import com.example.erjohnandroid.R
import com.example.erjohnandroid.database.Model.*
import com.example.erjohnandroid.database.Model.externalDispatch.SavedDispatchInfo
import com.example.erjohnandroid.database.viewmodel.RoomViewModel
import com.example.erjohnandroid.database.viewmodel.externalViewModel
import com.example.erjohnandroid.databinding.ActivityGetSynchingBinding
import com.example.erjohnandroid.domain.model.response.*
import com.example.erjohnandroid.presenter.viewmodel.networkViewModel
import com.example.erjohnandroid.printer.ThreadPoolManager
import com.example.erjohnandroid.printer.printerUtils.BytesUtil
import com.example.erjohnandroid.printer.printerUtils.HandlerUtils
import com.example.erjohnandroid.util.GlobalVariable
import com.example.erjohnandroid.util.ResultState
import com.example.erjohnandroid.util.showCustomToast
import com.example.erjohnandroid.util.startActivityWithAnimation
import com.iposprinter.iposprinterservice.IPosPrinterCallback
import com.iposprinter.iposprinterservice.IPosPrinterService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GetSynchingActivity : AppCompatActivity() {
    private var _binding:ActivityGetSynchingBinding?= null
    private val viewModel: networkViewModel by viewModels()
    private val dbViewmodel:RoomViewModel by viewModels()
    private val externalViewmodel:externalViewModel by viewModels()

    var text:String="Please wait getting Lines data..."

    private val PRINTER_NORMAL = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding= ActivityGetSynchingBinding.inflate(layoutInflater)
        setContentView(_binding!!.root)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        try {
            dbViewmodel.truncateBeforeUpdate()
        }catch (e:java.lang.Exception){
            Toast.makeText(this,"Error on Local DB !! ${e.message}",Toast.LENGTH_LONG).show()
        }


        _binding!!.btnGetAlldata.setOnClickListener {
            viewModel.getAllLines(GlobalVariable.token!!)
            _binding!!.btnGetAlldata.isEnabled=false
        }


    }


    override fun onStart() {
        super.onStart()
        viewModel.allLines.observe(this, Observer {
            state-> ProcessLines(state)
        })

        viewModel.companies.observe(this, Observer {
            state-> ProcessCompanies(state)
        })

        viewModel.businfo.observe(this, Observer {
            state->ProcessBusinfo(state)
        })

        viewModel.companyroles.observe(this, Observer {
            state-> ProcessCompanyRole(state)
        })

        viewModel.expensestype.observe(this, Observer {
            state-> ProcessExpensesType(state)
        })

        viewModel.passengertype.observe(this, Observer {
                state-> ProcessPassengertype(state)
        })

        viewModel.witholdingtype.observe(this, Observer {
                state-> PRocesswitholding(state)
        })

        viewModel.allHotspots.observe(this, Observer {
          state->ProcessHotspot(state)
        })

        viewModel.mpadunits.observe(this, Observer {
            state -> ProcessMpadUnits(state)
        })
        viewModel.allTerminals.observe(this,Observer{
            state -> ProcessTerminals(state)

        })

        viewModel.fare.observe(this , Observer {
            state -> ProcessFares(state)
        })

        viewModel.allfarebykm.observe(this, Observer {
            state -> processFareByKm(state)
        })
    }

    private var linesegment:ArrayList<LineSegment>?= ArrayList<LineSegment>()
    private  var line:ArrayList<LinesItem>?= ArrayList<LinesItem>()


    private  var dbline:ArrayList<LinesTable>?= arrayListOf()
    private  var dblinesegment:ArrayList<LineSegmentTable>?= arrayListOf()
    private var dbCompany:ArrayList<CompaniesTable>?= arrayListOf()
    private var dbBusinfo:ArrayList<BusInfoTableItem>?= arrayListOf()
    private var dbCompanyrole:ArrayList<CompanyRolesTable>?= arrayListOf()
    private var dbEmployees:ArrayList<EmployeesTable>?= arrayListOf()
    private var dbExpensestype:ArrayList<ExpensesTypeTable>?= arrayListOf()
    private var dbPassengertype:ArrayList<PassengerTypeTable>?= arrayListOf()
    private var dbWitholdingtype:ArrayList<WitholdingTypeTable>?= arrayListOf()
    private var dbHotspots:ArrayList<HotSpotsTable>?= arrayListOf()
    private var dbMpadUnits:ArrayList<mPadUnitsTable>?= arrayListOf()
    private var dbTerminals:ArrayList<TerminalTable>?= arrayListOf()
    private var dbfare:FareTable?= null
    private  var dbFarebykm:ArrayList<FareByKm>?= arrayListOf()

    private fun ProcessLines(state: ResultState<ArrayList<LinesItem>>?){
        when(state){
            is ResultState.Loading ->{_binding!!.txtGetsynchingtext.text=text}
            is ResultState.Success ->{

                if(state.data!=null) {
                    state.data.forEach {
                        line?.add(it)
                       it.lineSegments.forEach {
                           linesegment?.add(it)
                       }

                    }

                    dbline?.addAll(line?.map { it ->
                        LinesTable(
                            id = it.id,
                            name = it.name,
                            remarks = it.remarks,
                            tag = it.tag
                        )
                    }?: emptyList())

                 dblinesegment?.addAll(linesegment?.map { it ->
                     LineSegmentTable(
                         id = it.id,
                         kmPoint = it.kmPoint,
                         lineId = it.lineId,
                         name = it.name,
                         remarks = it.remarks,
                         tag = it.tag,
                         LineSegmentid = it.id
                     )
                 }?: emptyList())


                    viewModel.getCompanies(GlobalVariable.token!!)

                }

            }
            is ResultState.Error->{
              Toast.makeText(this,"Error!! ${state.exception}",Toast.LENGTH_LONG).show()


            }
            else -> {}
        }

    }

    private fun ProcessCompanies(state: ResultState<ArrayList<CompaniesItem>>?){
        when(state){
            is ResultState.Loading ->{_binding!!.txtGetsynchingtext.append("\nGetting companies...")}
            is ResultState.Success->{
                if(state.data!=null) {

                    dbCompany?.addAll(state.data?.map { it ->
                        CompaniesTable(
                            companyName = it.companyName,
                            id = it.id,
                            remarks = it.remarks,
                            tag = it.tag,
                            CompaniesId = it.id
                        )
                    }?: emptyList())
                    viewModel.getBusinfo(GlobalVariable.token!!)
                }
            }
            is ResultState.Error->{Toast.makeText(this,"Error!! ${state.exception}",Toast.LENGTH_LONG).show()}
            else -> {}
        }
    }

    private fun ProcessBusinfo(state: ResultState<ArrayList<BusInfos>>?){
        when(state){
            is ResultState.Loading ->{_binding!!.txtGetsynchingtext.append("\nGetting Bus information....")}
            is ResultState.Success-> {
                if (state.data != null) {
                    dbBusinfo?.addAll(state.data?.map {
                        it -> BusInfoTableItem(
                        BusInfoId =it.id,
                        busNumber = it.busNumber,
                        id = it.id,
                        plateNumber = it.plateNumber,
                        companyId = 2,
                        busTypeId = 1
                        )
                    }?: emptyList())
                   viewModel.getCompanyRoles(GlobalVariable.token!!)
                }
            }
            is ResultState.Error->{ Toast(this).showCustomToast(state.exception.toString(),this)}
            else -> {}




        }
    }

    private fun ProcessCompanyRole(state: ResultState<ArrayList<CompanyRolesItem>>?){
        when(state){
            is ResultState.Loading ->{_binding!!.txtGetsynchingtext.append("\nGetting Employee roles....")}
            is ResultState.Success-> {
                if (state.data != null) {
                    dbCompanyrole?.addAll(state.data?.map {
                        it -> CompanyRolesTable(
                        EmployeeroleId = it.id,
                        id = it.id,
                        name = it.name,
                        tag = it.tag
                        )
                    }?: emptyList())

                    state.data?.map { it ->
                        dbEmployees?.addAll(it.employee?.map { it ->
                            EmployeesTable(
                                EmployeeId = it.id,
                                companyRolesId = it.companyRolesId,
                                id = it.id,
                                lastName = it.lastName,
                                name = it.name,
                                pin = it.pin
                            )
                        }?: emptyList())
                    }

//                    state.data.forEach {
//                        it.employee.forEach {
//                            var methodemp= EmployeesTable(
//                                EmployeeId = it.id,
//                                companyRolesId = it.companyRolesId,
//                                id = it.id,
//                                lastName = it.lastName,
//                                name = it.name,
//                                pin = it.pin
//                            )
//                            dbEmployees?.add(methodemp)
//                        }
//                    }
                    viewModel.getExpensesTYpe(GlobalVariable.token!!)
                }
            }
            is ResultState.Error->{Toast(this).showCustomToast(state.exception.toString(),this)}
            else -> {}
        }
    }

    private fun ProcessExpensesType(state: ResultState<ArrayList<ExpensesTypesItem>>?){
        when(state){
            is ResultState.Loading ->{_binding!!.txtGetsynchingtext.append("\nGetting Expenses types....")}
            is ResultState.Success-> {
                if (state.data != null) {
                    dbExpensestype?.addAll(state.data?.map { it ->
                        ExpensesTypeTable(
                            ExpensesTypeId = it.id,
                            id = it.id,
                            name = it.name,
                            tag = it.tag
                        )
                    }?: emptyList())
                }
                viewModel.getPassengerType(GlobalVariable.token!!)
            }
            is ResultState.Error->{Toast(this).showCustomToast(state.exception.toString(),this)}
            else -> {}
        }
    }

    private fun ProcessPassengertype(state: ResultState<ArrayList<PassengerTypeItem>>?){
        when(state){
            is ResultState.Loading ->{_binding!!.txtGetsynchingtext.append("\nGetting passenger type....")}
            is ResultState.Success-> {
                if (state.data != null) {
                    dbPassengertype?.addAll(state.data?.map {
                        it -> PassengerTypeTable(
                        PassengerTypeId = it.id,
                        discount = it.discount.toDouble(),
                        id = it.id,
                        name = it.name,
                        tag = it.tag,
                        )
                    }?: emptyList())
                    viewModel.getWitholdingType(GlobalVariable.token!!)
                }
            }
            is ResultState.Error->{Toast(this).showCustomToast(state.exception.toString(),this)}
            else -> {}
        }
    }

    private fun PRocesswitholding(state: ResultState<ArrayList<WitholdingTypesItem>>?){
        when(state){
            is ResultState.Loading ->{_binding!!.txtGetsynchingtext.append("\nGetting witholding type....")}
            is ResultState.Success-> {
                dbWitholdingtype?.addAll(state.data?.map { it ->
                    WitholdingTypeTable(
                        WitholdingTypeId = it.id,
                        id = it.id,
                        type = it.type
                    )
                }?: emptyList())
                viewModel.getAllHotspots(GlobalVariable.token!!)
            }
            is ResultState.Error->{Toast(this).showCustomToast(state.exception.toString(),this)}
            else -> {}
        }
    }

    private fun ProcessHotspot(state: ResultState<ArrayList<HotSpotItem>>?){
        when(state){
            is ResultState.Loading ->{ _binding!!.txtGetsynchingtext.append("\nGetting Hotspots....")}
            is ResultState.Success-> {
                if (state.data != null) {
                    dbHotspots?.addAll(state.data?.map { it ->
                        HotSpotsTable(
                            fare = it.fare,
                            id = it.id,
                            lineid = it.lineid,
                            modeid = it.modeid,
                            namE2 = it.namE2,
                            name = it.name,
                            pointfrom = it.pointfrom,
                            pointto = it.pointto,
                            tag = it.tag
                        )
                    }?: emptyList())
                    viewModel.getMpadUnits(GlobalVariable.token!!)
                }
            }
            is ResultState.Error->{Toast(this).showCustomToast(state.exception.toString(),this)}
            else -> {}
        }
    }

    private fun ProcessMpadUnits(state: ResultState<ArrayList<mPadUnitsItem>>?){
        when(state){
            is ResultState.Loading ->{_binding!!.txtGetsynchingtext.append("\nGetting MpadUnits....")}
            is ResultState.Success-> {
                if (state.data != null) {
                   dbMpadUnits?.addAll(state.data?.map { it ->
                       mPadUnitsTable(
                           id = it.id,
                           machineName = it.machineName,
                           name = it.name,
                           permit = it.permit,
                           permitNumber = it.permitNumber,
                           serialNumber = it.serialNumber,
                           tag = it.tag
                       )
                   }?: emptyList())
                    viewModel.getAllTerminals(GlobalVariable.token!!)
                }
            }
            is ResultState.Error->{Toast(this).showCustomToast(state.exception.toString(),this)}
            else -> {}
        }
    }

    private fun ProcessTerminals(state: ResultState<ArrayList<TerminalsItem>>?){
        when(state){
            is ResultState.Loading ->{ _binding!!.txtGetsynchingtext.append("\nGetting Terminals....")}
            is ResultState.Success-> {
                if (state.data != null) {
                    dbTerminals?.addAll(state.data?.map { it ->
                        TerminalTable(
                            id = it.id!!,
                            name = it.name!!,
                            description = it.description ?: "null",
                            TerminalId = 0
                        )
                    } ?: emptyList())
                    viewModel.getFares(GlobalVariable.token!!, 1)
                }
            }
            is ResultState.Error->{Toast(this).showCustomToast(state.exception.toString(),this)}
            else -> {}
        }
    }

    private fun ProcessFares(state: ResultState<Fares>?){
        when(state){
            is ResultState.Loading ->{_binding!!.txtGetsynchingtext.append("\nGetting Fare....")}
            is ResultState.Success-> {
                     if (state.data != null) {
                        var method= FareTable(
                            FareId = 0,
                            baseAmount = state.data.baseAmount,
                            discountAmount = state.data.discountAmount,
                            exceedAmount = state.data.exceedAmount,
                            id = state.data.id,
                            name = state.data.name,
                            specialExceedAmount = state.data.specialExceedAmount
                        )
                         dbfare = method
                        viewModel.getAllFarebykm(GlobalVariable.token!!)
                    }
            }
            is ResultState.Error->{Toast(this).showCustomToast(state.exception.toString(),this)}
            else -> {}
        }
    }

    private fun processFareByKm(state: ResultState<List<FareByKmItem>>?){
        var increment=0
        when(state){
            is ResultState.Loading ->{_binding!!.txtGetsynchingtext.append("\nGetting Fare matrix....")}
            is ResultState.Success-> {
                if (state.data != null) {
                    state.data.forEach {
                        increment++
                        var method= FareByKm(
                            lineid = it.lineid,
                            totalkm = it.totalkm,
                            discountrate = it.discountrate,
                            amount = it.amount,
                            lowerkmlimit = it.lowekmlimit,
                            upperkmlimit = it.upperkmlimit,
                            id = it.id,
                            farekmId = increment
                        )
                        dbFarebykm?.add(method)
                    }
                    InsertDB()
                }
            }
            is ResultState.Error->{Toast(this).showCustomToast(state.exception.toString(),this)}
            else -> {}
        }
    }


    val InsertDB:()-> Unit ={
        try {
            _binding!!.txtGetsynchingtext.append("\nINSERTING DATA TO DATABASE....")
            dbViewmodel.insertAllLines(dbline!!)
            dbViewmodel.insertLinesegmentBulk(dblinesegment!!)
            dbViewmodel.insertCompany(dbCompany!!)
            dbViewmodel.insertBusinfo(dbBusinfo!!)
            dbViewmodel.insertCompanyRoles(dbCompanyrole!!)
            dbViewmodel.insertEmployeeBulk(dbEmployees!!)
            dbViewmodel.insertExpensestypeBulk(dbExpensestype!!)
            dbViewmodel.insertPassengerTypeBUlk(dbPassengertype!!)
            dbViewmodel.insertWitholdingtypebulk(dbWitholdingtype!!)
            dbViewmodel.insertAllHotspots(dbHotspots!!)
            dbViewmodel.insertMpadUnits(dbMpadUnits!!)
            dbViewmodel.insertTerminalsBulk(dbTerminals!!)
            dbViewmodel.insertAllFarebykm(dbFarebykm!!)
            lifecycleScope.launch {
                dbViewmodel.insertfare(dbfare!!)
            }


            var method= TicketCounterTable(
                ticketnumber = 1,
                ingressoRefId = 0,
                Id = 0

            )

            var methodSavedDispatch= SavedDispatchInfo(
                busNumber = "1111",
                conductorName = "Conductor",
                isDispatched = false,
                dispatcherName = "dispatcher",
                driverName = "driver",
                line = "testline",
                LineId = 1,
                mPadUnit = "testdevice",
               // ingressoRefId = 0,
                reverse = 0,
                orginalTicketnumber = 0,
                direction = "nothing",
                ingressoRefId = 0,
                machineName = "0",
                permitNumber = "0",
                serialNumber = "0"

            )

            externalViewmodel.inserticketnu(method)
            externalViewmodel.insertSavedDispatched(methodSavedDispatch)
            GlobalVariable.saveLogreportlogin("Data successfully saved")
            showCustomToast(this, "Fetch Data Success")
            finish()
            startActivityWithAnimation<MainActivity>(R.anim.screenslideright, R.anim.screen_slide_out_left)

        }catch (e:java.lang.Exception){
            Toast(this).showCustomToast("Contact AZ Services - ${e.message.toString()}",this)
            GlobalVariable.saveLogreport("Error on data synching, ${e.message}")
        }
    }
}