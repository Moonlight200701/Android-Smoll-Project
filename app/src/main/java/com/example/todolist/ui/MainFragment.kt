package com.example.todolist.ui

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.dataSource.TaskDataSource
import com.example.todolist.databinding.FragmentMainBinding
import com.example.todolist.viewmodel.MainViewModel

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
        private const val CREATE_NEW_TASK = 1000
    }
    // Implement a viewBinding class for this fragment
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: MainViewModel
    private lateinit var recyclerView: RecyclerView
    private val adapter by lazy { TaskListAdapter() }
    private var isLinearLayoutManager = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(false)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView = binding.recyclerTask
        updateList()
        insertListeners()
        chooseLayout()
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        // TODO: Use the ViewModel
    }

    // Destroy view cause it's not used anymore
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun chooseLayout()
    {
        when(isLinearLayoutManager){
            true -> {
                recyclerView.layoutManager = LinearLayoutManager(context)
                recyclerView.adapter = TaskListAdapter()
            }
            false -> {
                recyclerView.layoutManager = GridLayoutManager(context, 4)
                recyclerView.adapter = TaskListAdapter()
            }
        }
    }
    private fun updateList() {
        val list = TaskDataSource.getList()
        if (list.isEmpty()) {
            binding.emptyInclude.stateEmptyCs.visibility = View.VISIBLE
        } else {
            binding.emptyInclude.stateEmptyCs.visibility = View.GONE
        }
        adapter.submitList(list)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CREATE_NEW_TASK && resultCode == Activity.RESULT_OK) updateList()
    }
    private fun insertListeners() {
        binding.fab.setOnClickListener {
            startActivityForResult(Intent(context, AddTaskActivity::class.java),
                CREATE_NEW_TASK
            )
        }
        adapter.listenerEdit = {
            val intent = Intent(context, AddTaskActivity::class.java)
            intent.putExtra(AddTaskActivity.TASK_ID, it.id)
            startActivityForResult(intent, CREATE_NEW_TASK)
        }
        adapter.listenerDelete = {
            TaskDataSource.deleteTask(it)
            updateList()
        }
    }
//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//    inflater.inflate(R.menu.popup_menu, menu)
//    }
}