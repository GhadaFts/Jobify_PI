package com.example.jobify.ui.posts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.jobify.model.Job

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EditJobDialog(job: Job, onDismiss: () -> Unit, onSave: (Job) -> Unit) {
    val jobTypes = listOf("Full-time", "Part-time", "Contract", "Freelance")
    val experienceLevels = listOf("Entry-level", "Mid-level", "Senior-level", "Lead")
    val jobStatusOptions = listOf("open", "new", "hot job", "limited openings", "actively hiring", "urgent hiring")

    var title by remember { mutableStateOf(job.title) }
    var company by remember { mutableStateOf(job.company) }
    var location by remember { mutableStateOf(job.location) }
    var jobType by remember { mutableStateOf(job.jobType) }
    var experience by remember { mutableStateOf(job.experience) }
    var salary by remember { mutableStateOf(job.salaryRange) }
    var description by remember { mutableStateOf(job.shortDescription) }
    var skills by remember { mutableStateOf(job.skills) }
    var requirements by remember { mutableStateOf(job.requirements) }
    var badge by remember { mutableStateOf(job.badge ?: jobStatusOptions.first()) }
    var published by remember { mutableStateOf(job.published) }

    Dialog(onDismissRequest = onDismiss) {
        Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(Modifier.padding(24.dp).verticalScroll(rememberScrollState())) {
                Text("Edit Job", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(16.dp))

                val textFieldColors = TextFieldDefaults.outlinedTextFieldColors(containerColor = Color.White)

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(title, { title = it }, label = { Text("Job Title *") }, modifier = Modifier.weight(1f), colors = textFieldColors)
                    OutlinedTextField(company, { company = it }, label = { Text("Company *") }, modifier = Modifier.weight(1f), colors = textFieldColors)
                }
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(location, { location = it }, label = { Text("Location *") }, modifier = Modifier.weight(1f), colors = textFieldColors)
                    DropdownInput(jobType, { jobType = it }, jobTypes, "Job Type *", Modifier.weight(1f))
                }
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    DropdownInput(experience, { experience = it }, experienceLevels, "Experience Level *", Modifier.weight(1f))
                    OutlinedTextField(salary, { salary = it }, label = { Text("Salary Range *") }, modifier = Modifier.weight(1f), colors = textFieldColors)
                }

                Spacer(Modifier.height(8.dp))
                OutlinedTextField(description, { description = it }, label = { Text("Job Description *") }, modifier = Modifier.fillMaxWidth().height(120.dp), colors = textFieldColors)

                EditableChipSection("Required Skills", skills) { skills = it }
                EditableListSection("Requirements", requirements) { requirements = it }

                DropdownInput(badge, { badge = it }, jobStatusOptions, "Job Status")

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = published, onCheckedChange = { published = it })
                    Text("Published")
                }

                Spacer(Modifier.height(24.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    OutlinedButton(onClick = onDismiss) { Text("Cancel") }
                    Spacer(Modifier.width(8.dp))
                    Button(onClick = {
                        onSave(job.copy(
                            title = title, company = company, location = location, jobType = jobType, experience = experience,
                            salaryRange = salary, shortDescription = description, skills = skills, requirements = requirements,
                            badge = badge, published = published
                        ))
                    }) { Text("Save Changes") }
                }
            }
        }
    }
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun JobDetailsDialog(job: Job, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(modifier = Modifier.padding(24.dp).verticalScroll(rememberScrollState())) {
                Text(job.title, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                Spacer(Modifier.height(4.dp))
                Text(job.company, color = MaterialTheme.colorScheme.primary, fontSize = 16.sp)
                Spacer(Modifier.height(16.dp))
                FlowRow(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    DetailInfoChip(Icons.Default.LocationOn, job.location)
                    LabelChip(label = job.jobType)
                    DetailInfoChip(Icons.Default.AttachMoney, job.salaryRange)
                    DetailInfoChip(Icons.Default.People, "${job.applicantsCount} applicants")
                }
                Spacer(Modifier.height(24.dp))
                SectionTitle("Job Description")
                Text(job.shortDescription, fontSize = 16.sp, lineHeight = 24.sp)
                Spacer(Modifier.height(24.dp))
                SectionTitle("Required Skills")
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    job.skills.forEach { LabelChip(label = it) }
                }
                Spacer(Modifier.height(24.dp))
                SectionTitle("Requirements")
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    job.requirements.forEach { RequirementItem(it) }
                }
                Spacer(Modifier.height(24.dp))
                OutlinedButton(onClick = onDismiss, modifier = Modifier.align(Alignment.End)) { Text("Close") }
            }
        }
    }
}
