package com.example.erjohnandroid.presenter.Activity.mainactivity

import android.os.Bundle
import android.os.RemoteException
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.erjohnandroid.R
import com.example.erjohnandroid.database.Model.*
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
import com.example.erjohnandroid.util.startActivityWithAnimation
import com.iposprinter.iposprinterservice.IPosPrinterCallback
import com.iposprinter.iposprinterservice.IPosPrinterService
import dagger.hilt.android.AndroidEntryPoint

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

    private fun ProcessLines(state: ResultState<ArrayList<LinesItem>>?){
        when(state){
            is ResultState.Loading ->{
                //showCustomProgressDialog()
                _binding!!.txtGetsynchingtext.text=text
            }
            is ResultState.Success->{

                if(state.data!=null) {
                    val data= state.data
                    state.data.forEach {
                        line?.add(it)
                       it.lineSegments.forEach {
                           linesegment?.add(it)
                       }

                    }

                   line?.forEach {
                       var method= LinesTable(
                          id = it.id,
                          name = it.name,
                          remarks = it.remarks,
                           tag = it.tag
                       )
                       dbline?.add(method)


                   }

                    linesegment?.forEach {
                        var methodsegment= LineSegmentTable(
                            id = it.id,
                            kmPoint = it.kmPoint,
                            lineId = it.lineId,
                            name = it.name,
                            remarks = it.remarks,
                            tag = it.tag,
                            LineSegmentid = it.id

                        )
                        dblinesegment?.add(methodsegment)
                    }

                    Log.d("line",dbline?.size.toString())
                    Log.d("line1",dblinesegment?.size.toString())
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
            is ResultState.Loading ->{
                _binding!!.txtGetsynchingtext.append("\nGetting companies...")
            }
            is ResultState.Success->{
                if(state.data!=null) {
                    state.data.forEach {
                        var method= CompaniesTable(
                            companyName = it.companyName,
                            id = it.id,
                            remarks = it.remarks,
                            tag = it.tag,
                            CompaniesId = it.id
                        )
                        dbCompany?.add(method)
                    }
                    Log.d("compa",dbCompany?.size.toString())
                        viewModel.getBusinfo(GlobalVariable.token!!)
                }

            }
            is ResultState.Error->{
                Toast.makeText(this,"Error!! ${state.exception}",Toast.LENGTH_LONG).show()


            }
            else -> {}
        }

    }

    private fun ProcessBusinfo(state: ResultState<ArrayList<BusInfos>>?){
        when(state){
            is ResultState.Loading ->{
                _binding!!.txtGetsynchingtext.append("\nGetting Bus information....")
            }
            is ResultState.Success-> {
                if (state.data != null) {
                    state.data.forEach {
                        var method= BusInfoTableItem(
                            BusInfoId =it.id,
                            busNumber = it.busNumber,
                            id = it.id,
                            plateNumber = it.plateNumber,
                            companyId = 2,
                            busTypeId = 1
                        )
                        dbBusinfo?.add(method)
                    }
                    Log.d("d",dbBusinfo?.size.toString())
                        viewModel.getCompanyRoles(GlobalVariable.token!!)

                }
            }
            is ResultState.Error->{
                Toast.makeText(this,"Error!! ${state.exception}",Toast.LENGTH_LONG).show()


            }
            else -> {}
        }
    }

    private fun ProcessCompanyRole(state: ResultState<ArrayList<CompanyRolesItem>>?){
        when(state){
            is ResultState.Loading ->{
                _binding!!.txtGetsynchingtext.append("\nGetting Employee roles....")
            }
            is ResultState.Success-> {
                if (state.data != null) {

                    state.data.forEach {
                      var method = CompanyRolesTable(
                          EmployeeroleId = it.id,
                          id = it.id,
                          name = it.name,
                          tag = it.tag
                      )
                        dbCompanyrole?.add(method)
                    }

                    state.data.forEach {
                        it.employee.forEach {
                            var methodemp= EmployeesTable(
                                EmployeeId = it.id,
                                companyRolesId = it.companyRolesId,
                                id = it.id,
                                lastName = it.lastName,
                                name = it.name,
                                pin = it.pin
                            )
                            dbEmployees?.add(methodemp)
                        }
                    }
                   Log.d("role",dbCompanyrole?.size.toString())
                    Log.d("emp",dbEmployees?.size.toString())
                    viewModel.getExpensesTYpe(GlobalVariable.token!!)

                }
            }
            is ResultState.Error->{
                Toast.makeText(this,"Error!! ${state.exception}",Toast.LENGTH_LONG).show()


            }
            else -> {}
        }
    }

    private fun ProcessExpensesType(state: ResultState<ArrayList<ExpensesTypesItem>>?){
        when(state){
            is ResultState.Loading ->{
                _binding!!.txtGetsynchingtext.append("\nGetting Expenses types....")
            }
            is ResultState.Success-> {

                if (state.data != null) {
                    state.data.forEach {
                        var method= ExpensesTypeTable(
                            ExpensesTypeId = it.id,
                            id = it.id,
                            name = it.name,
                            tag = it.tag
                        )
                        dbExpensestype?.add(method)
                    }

                }
                Log.d("expetype",dbExpensestype?.size.toString())
                viewModel.getPassengerType(GlobalVariable.token!!)
            }
            is ResultState.Error->{
                Toast.makeText(this,"Error!! ${state.exception}",Toast.LENGTH_LONG).show()


            }
            else -> {}
        }
    }

    private fun ProcessPassengertype(state: ResultState<ArrayList<PassengerTypeItem>>?){
        when(state){
            is ResultState.Loading ->{
                _binding!!.txtGetsynchingtext.append("\nGetting passenger type....")
            }
            is ResultState.Success-> {

                if (state.data != null) {
                   state.data.forEach {
                       var method = PassengerTypeTable(
                           PassengerTypeId = it.id,
                           discount = it.discount.toDouble(),
                           id = it.id,
                           name = it.name,
                           tag = it.tag,

                       )
                       dbPassengertype?.add(method)
                   }
                    Log.d("passtype",dbPassengertype?.size.toString())
                    viewModel.getWitholdingType(GlobalVariable.token!!)
                }

            }
            is ResultState.Error->{
                Toast.makeText(this,"Error!! ${state.exception}",Toast.LENGTH_LONG).show()


            }
            else -> {}
        }
    }

    private fun PRocesswitholding(state: ResultState<ArrayList<WitholdingTypesItem>>?){
        when(state){
            is ResultState.Loading ->{
                _binding!!.txtGetsynchingtext.append("\nGetting witholding type....")
            }
            is ResultState.Success-> {

                if (state.data != null) {
                    state.data.forEach {
                       var method= WitholdingTypeTable(
                            WitholdingTypeId = it.id,
                           id = it.id,
                           type = it.type

                       )
                        dbWitholdingtype?.add(method)
                    }
                    viewModel.getAllHotspots(GlobalVariable.token!!)
                    Log.d("withold",dbWitholdingtype?.size.toString())
                    //InsertDB()
                }

            }
            is ResultState.Error->{
                Toast.makeText(this,"Error!! ${state.exception}",Toast.LENGTH_LONG).show()


            }
            else -> {}
        }
    }

    private fun ProcessHotspot(state: ResultState<ArrayList<HotSpotItem>>?){
        when(state){
            is ResultState.Loading ->{
                _binding!!.txtGetsynchingtext.append("\nGetting Hotspots....")
            }
            is ResultState.Success-> {

                if (state.data != null) {
                    Log.d("hsize",state.data.size.toString())
                    state.data.forEach {
                        var method= HotSpotsTable(
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
                        dbHotspots?.add(method)
                    }

                    Log.d("hotspots",dbWitholdingtype?.size.toString())
                    InsertDB()
                }

            }
            is ResultState.Error->{
                Toast.makeText(this,"Error!! ${state.exception}",Toast.LENGTH_LONG).show()


            }
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

            var method= TicketCounterTable(
                ticketnumber = 1,
                ingressoRefId = 0,
                Id = 0

            )
            externalViewmodel.inserticketnu(method)
            finish()
            startActivityWithAnimation<MainActivity>(R.anim.screenslideright, R.anim.screen_slide_out_left)

        }catch (e:java.lang.Exception){
            Log.e("erro",e.localizedMessage)
        }



    }
}